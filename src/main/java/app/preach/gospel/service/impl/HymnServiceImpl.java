package app.preach.gospel.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.VersionMismatchException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.entity.Hymn;
import app.preach.gospel.entity.HymnsWork;
import app.preach.gospel.entity.Student;
import app.preach.gospel.repository.HymnRepository;
import app.preach.gospel.repository.HymnsWorkRepository;
import app.preach.gospel.repository.StudentRepository;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoBeanUtils;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.LineNumber;
import app.preach.gospel.utils.Pagination;
import app.preach.gospel.utils.SnowflakeUtils;
import jakarta.persistence.PersistenceException;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 賛美歌サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HymnServiceImpl implements IHymnService {

	/**
	 * 共通検索条件
	 */
	protected static final Specification<Hymn> COMMON_CONDITION = (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("visibleFlg"), Boolean.TRUE);

	/**
	 * KOMORAN-API
	 */
	private static final Komoran KOMORAN = new Komoran(DEFAULT_MODEL.FULL);

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * ランドム選択
	 */
	private static final Random RANDOM = new Random();

	/**
	 * 日本語名称
	 */
	private static final String NAME_JP = "nameJp";

	/**
	 * 韓国語名称
	 */
	private static final String NAME_KR = "nameKr";

	/**
	 * 怪しいキーワードリスト
	 */
	private static final String[] STRANGE_ARRAY = { "insert", "delete", "update", "create", "drop", "#", "$", "%", "&",
			"(", ")", "\"", "\'", "@", ":", "select" };

	/**
	 * コサイン類似度を計算する
	 *
	 * @param vectorA ベクターA
	 * @param vectorB ベクターB
	 * @return コサイン類似度
	 */
	private static double cosineSimilarity(final double @NotNull [] vectorA, final double[] vectorB) {
		double dotProduct = 0.00;
		double normA = 0.00;
		double normB = 0.00;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		if ((normA == 0) || (normB == 0)) {
			return 0; // 避免除0
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

//	/**
//	 * jaccard類似度を計算する
//	 *
//	 * @param vectorA ベクターA
//	 * @param vectorB ベクターB
//	 * @return コサイン類似度
//	 */
//	private static double jaccardSimilarity(final Set<String> setA, final Set<String> setB) {
//		if (CollectionUtils.isEmpty(setA) && CollectionUtils.isEmpty(setB)) {
//			return 0.00;
//		}
//		final Set<String> intersection = new HashSet<>(setA);
//		intersection.retainAll(setB);
//		final int unionSize = (setA.size() + setB.size()) - intersection.size();
//		return unionSize == 0 ? 0.00 : intersection.size() / unionSize;
//	}

	/**
	 * 賛美歌情報管理リポジトリ
	 */
	private final HymnRepository hymnRepository;

	/**
	 * 賛美歌セリフ情報リポジトリ
	 */
	private final HymnsWorkRepository hymnsWorkRepository;

	/**
	 * 奉仕者管理リポジトリ
	 */
	private final StudentRepository studentRepository;

	@Override
	public CoResult<Integer, PersistenceException> checkDuplicated(final String id, final String nameJp) {
		try {
			final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(NAME_JP), nameJp);
			if (CoProjectUtils.isDigital(id)) {
				final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
						.notEqual(root.get("id"), Long.parseLong(id));
				final long duplicated = this.hymnRepository
						.count(COMMON_CONDITION.and(specification).and(specification1));
				return CoResult.ok((int) duplicated);
			}
			final long duplicated = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<Integer, PersistenceException> checkDuplicated2(final String id, final String nameKr) {
		try {
			final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(NAME_KR), nameKr);
			if (CoProjectUtils.isDigital(id)) {
				final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
						.notEqual(root.get("id"), Long.parseLong(id));
				final long duplicated = this.hymnRepository
						.count(COMMON_CONDITION.and(specification).and(specification1));
				return CoResult.ok((int) duplicated);
			}
			final long duplicated = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	/**
	 * TF-IDFコサイン類似度を計算する
	 *
	 * @param target 目標テキスト
	 * @param hymns  他の賛美歌テキスト
	 * @return Map<Hymn, Double>
	 */
	private @NotNull Map<Hymn, Double> computeTfIdfOfSerivies(final String target, final @NotNull List<Hymn> hymns) {
		final List<String> hymnTextList = hymns.stream().map(Hymn::getSerif).toList();
		final List<String> allTexts = new ArrayList<>(hymnTextList);
		allTexts.add(target); // 将目标文本也加入计算
		// 1. 进行韩语分词处理
		final List<List<String>> tokenizedTexts = new ArrayList<>();
		for (final String text : allTexts) {
			tokenizedTexts.add(this.tokenizeKoreanText(text));
		}
		// 2. 构建词汇表 (Vocabulary)
		final List<String> vocabulary = new ArrayList<>();
		for (final List<String> tokens : tokenizedTexts) {
			vocabulary.addAll(tokens);
		}
		// 3. 计算 TF 矩阵
		final double[][] tfMatrix = new double[allTexts.size()][vocabulary.size()];
		for (int i = 0; i < tokenizedTexts.size(); i++) {
			final List<String> words = tokenizedTexts.get(i);
			final Map<String, Integer> termCount = new LinkedHashMap<>();
			for (final String word : words) { // 単語の頻度を計算
				termCount.put(word, termCount.getOrDefault(word, 0) + 1);
			}
			for (int j = 0; j < vocabulary.size(); j++) {
				tfMatrix[i][j] = termCount.getOrDefault(vocabulary.get(j), 0);
			}
		}
		// 4. 计算 IDF 矩阵
		final double[] idfVector = new double[vocabulary.size()];
		for (int j = 0; j < vocabulary.size(); j++) {
			int docCount = 0;
			for (final List<String> tokens : tokenizedTexts) {
				if (tokens.contains(vocabulary.get(j))) {
					docCount++;
				}
			}
			idfVector[j] = Math.log((double) allTexts.size() / (docCount + 1)); // 避免除0
		}
		// 5. 计算 TF-IDF 矩阵
		final double[][] tfidfMatrix = new double[allTexts.size()][vocabulary.size()];
		for (int i = 0; i < allTexts.size(); i++) {
			for (int j = 0; j < vocabulary.size(); j++) {
				tfidfMatrix[i][j] = tfMatrix[i][j] * idfVector[j];
			}
		}
		// 6. 计算目标文本（最后一个）与其他文本的余弦相似度
		final double[] targetVector = tfidfMatrix[allTexts.size() - 1]; // 目标文本向量
		final Map<Hymn, Double> similarityMap = new LinkedHashMap<>();
		for (int i = 0; i < (allTexts.size() - 1); i++) {
			final double similarity = HymnServiceImpl.cosineSimilarity(targetVector, tfidfMatrix[i]);
			similarityMap.put(hymns.get(i), similarity);
		}
		return similarityMap;
	}

	@Override
	public @NotNull CoResult<HymnDto, PersistenceException> getHymnInfoById(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<HymnDto, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			final Specification<Student> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get("id"), val.getUpdatedUser());
			this.studentRepository.findOne(StudentServiceImpl.COMMON_CONDITION.and(specification2))
					.ifPresentOrElse(subVal -> {
						final ZonedDateTime zonedDateTime = val.getUpdatedTime()
								.atZoneSameInstant(ZoneOffset.ofHours(9));
						final HymnDto hymnDto = new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(),
								val.getSerif(), val.getLink(), val.getScore(), subVal.getUsername(),
								FORMATTER.format(zonedDateTime.toLocalDateTime()), null);
						result.setSelf(CoResult.ok(hymnDto));
					}, () -> result.setSelf(
							CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<Pagination<HymnDto>, PersistenceException> getHymnsByKeyword(final Integer pageNum,
			final String keyword) {
		final String searchStr = CoProjectUtils.getDetailKeyword(keyword);
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder.or(
				criteriaBuilder.like(root.get(NAME_JP), searchStr), criteriaBuilder.like(root.get(NAME_KR), searchStr));
		try {
			final long totalRecords = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			final PageRequest pageRequest = PageRequest.of(pageNum - 1, ProjectConstants.DEFAULT_PAGE_SIZE,
					Sort.by(Direction.ASC, "id"));
			final Page<Hymn> hymnsRecords = this.hymnRepository.findAll(COMMON_CONDITION.and(specification),
					pageRequest);
			final List<HymnDto> hymnDtos = hymnsRecords.getContent().stream()
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
							hymnsRecord.getScore(), null, null, null))
					.toList();
			return CoResult.ok(Pagination.of(hymnDtos, totalRecords, pageNum, ProjectConstants.DEFAULT_PAGE_SIZE));
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<List<HymnDto>, PersistenceException> getHymnsRandomFive(final String keyword) {
		try {
			if (CoProjectUtils.isEmpty(keyword)) {
				final List<Hymn> totalRecords = this.hymnRepository.findAll(COMMON_CONDITION);
				final List<HymnDto> hymnDtos = this.randomFiveLoop2(totalRecords).stream()
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
								hymnsRecord.getScore(), null, null, LineNumber.SNOWY))
						.toList();
				return CoResult.ok(hymnDtos);
			}
			for (final String starngement : STRANGE_ARRAY) {
				if (keyword.toLowerCase().contains(starngement) || (keyword.length() >= 100)) {
					final List<HymnDto> hymnDtos = this.hymnRepository.findForStrangement().stream()
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
									hymnsRecord.getScore(), null, null, LineNumber.SNOWY))
							.toList();
					log.error("怪しいキーワード： " + keyword);
					return CoResult.ok(hymnDtos);
				}
			}
			final List<HymnsWork> hymnsWorks = this.hymnRepository.findForUpdatedTime().stream().map(item -> {
				final HymnsWork hymnsWork = new HymnsWork();
				final String title = item.getNameJp();
				final String serif = item.getSerif();
				final String toKatakanaTitle = this.kanjiToKatakana(title);
				final String toKatakanaSerif = this.kanjiToKatakana(serif);
				hymnsWork.setId(item.getId());
				hymnsWork.setTitle(toKatakanaTitle);
				hymnsWork.setSerif(toKatakanaSerif);
				hymnsWork.setUpdatedTime(OffsetDateTime.now());
				return hymnsWork;
			}).toList();
			this.hymnsWorkRepository.saveAllAndFlush(hymnsWorks);
			final String kanjiToKatakanaKeyword = this.kanjiToKatakana(keyword);
			final String searchStr1 = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(keyword)
					.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
			final String searchKatakana1 = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(kanjiToKatakanaKeyword)
					.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
			final List<Hymn> hymnsTitle = this.hymnRepository.retrieveRandomFive1(searchStr1, searchKatakana1);
			final List<HymnDto> hymnDtos = new ArrayList<>();
			final List<HymnDto> hymnDtos0 = hymnsTitle.stream()
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
							hymnsRecord.getScore(), null, null, LineNumber.CADIMIUM))
					.toList();
			hymnDtos.addAll(hymnDtos0);
			final List<Hymn> hymns1 = this.hymnRepository.retrieveRandomFive2(searchStr1, searchKatakana1);
			final List<Long> titleIds = hymnsTitle.stream().map(Hymn::getId).toList();
			if (CollectionUtils.isEmpty(hymns1) || (hymns1.size() <= ProjectConstants.DEFAULT_PAGE_SIZE)) {
				final List<Hymn> hymns = new ArrayList<>();
				hymns.addAll(hymns1);
				final String searchStr2 = CoProjectUtils.getDetailKeyword(keyword);
				final String searchKatakana2 = CoProjectUtils.getDetailKeyword(kanjiToKatakanaKeyword);
				final List<Hymn> hymns2 = this.hymnRepository.retrieveRandomFive2(searchStr2, searchKatakana2);
				hymns.addAll(hymns2);
				final List<Hymn> hymns3 = hymns.stream().distinct().toList();
				final List<Long> ids1 = hymns1.stream().map(Hymn::getId).toList();
				if (CollectionUtils.isEmpty(hymns3) || (hymns3.size() <= ProjectConstants.DEFAULT_PAGE_SIZE)) {
					final List<Long> ids2 = hymns2.stream().map(Hymn::getId).toList();
					final List<Hymn> totalRecords = this.hymnRepository.findAll(COMMON_CONDITION);
					final List<Hymn> randomFiveLoop = this.randomFiveLoop(hymns, totalRecords);
					final List<HymnDto> hymnDtos1 = randomFiveLoop.stream()
							.filter(a -> !titleIds.contains(a.getId()) && ids1.contains(a.getId()))
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
									hymnsRecord.getScore(), null, null, LineNumber.BUNRGUNDY))
							.toList();
					hymnDtos.addAll(hymnDtos1);
					final List<HymnDto> hymnDtos2 = randomFiveLoop.stream().filter(
							a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId()) && ids2.contains(a.getId()))
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
									hymnsRecord.getScore(), null, null, LineNumber.NAPLES))
							.toList();
					hymnDtos.addAll(hymnDtos2);
					final List<HymnDto> hymnDtos3 = randomFiveLoop.stream()
							.filter(a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId())
									&& !ids2.contains(a.getId()))
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
									hymnsRecord.getScore(), null, null, LineNumber.SNOWY))
							.toList();
					hymnDtos.addAll(hymnDtos3);
					return CoResult.ok(hymnDtos.subList(0, 5));
				}
				final List<Hymn> randomFiveLoop = this.randomFiveLoop2(hymns3);
				final List<HymnDto> hymnDtos1 = randomFiveLoop.stream()
						.filter(a -> !titleIds.contains(a.getId()) && ids1.contains(a.getId()))
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
								hymnsRecord.getScore(), null, null, LineNumber.BUNRGUNDY))
						.toList();
				hymnDtos.addAll(hymnDtos1);
				final List<HymnDto> hymnDtos2 = randomFiveLoop.stream()
						.filter(a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId()))
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
								hymnsRecord.getScore(), null, null, LineNumber.NAPLES))
						.toList();
				hymnDtos.addAll(hymnDtos2);
				return CoResult.ok(hymnDtos.subList(0, 5));
			}
			final List<Hymn> randomFiveLoop2 = this.randomFiveLoop2(hymns1);
			final List<HymnDto> hymnDtos1 = randomFiveLoop2.stream().filter(a -> !titleIds.contains(a.getId()))
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
							hymnsRecord.getScore(), null, null, LineNumber.BUNRGUNDY))
					.toList();
			hymnDtos.addAll(hymnDtos1);
			return CoResult.ok(hymnDtos.subList(0, 5));
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<List<HymnDto>, PersistenceException> getKanumiList(final Long id) {
		final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<List<HymnDto>, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification1)).ifPresentOrElse(val -> {
			final List<HymnDto> hymnDtos = new ArrayList<>();
			hymnDtos.add(new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(), val.getSerif(),
					val.getLink(), val.getScore(), null, null, LineNumber.BUNRGUNDY));
			final Specification<Hymn> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
					.notEqual(root.get("id"), id);
			final List<Hymn> hymns = this.hymnRepository.findAll(COMMON_CONDITION.and(specification2),
					Sort.by(Direction.ASC, "id"));
			final Map<Hymn, Double> tfIdfOfSerivies = this.computeTfIdfOfSerivies(val.getSerif(), hymns);
			final List<Entry<Hymn, Double>> arrayList = new ArrayList<>(tfIdfOfSerivies.entrySet());
			arrayList.sort(Entry.comparingByValue(Comparator.reverseOrder()));
			final Hymn hymn1 = arrayList.get(0).getKey();
			final Hymn hymn2 = arrayList.get(1).getKey();
			final Hymn hymn3 = arrayList.get(2).getKey();
			hymnDtos.add(new HymnDto(hymn1.getId().toString(), hymn1.getNameJp(), hymn1.getNameKr(), hymn1.getSerif(),
					hymn1.getLink(), hymn1.getScore(), null, null, LineNumber.NAPLES));
			hymnDtos.add(new HymnDto(hymn2.getId().toString(), hymn2.getNameJp(), hymn2.getNameKr(), hymn2.getSerif(),
					hymn2.getLink(), hymn2.getScore(), null, null, LineNumber.NAPLES));
			hymnDtos.add(new HymnDto(hymn3.getId().toString(), hymn3.getNameJp(), hymn3.getNameKr(), hymn3.getSerif(),
					hymn3.getLink(), hymn3.getScore(), null, null, LineNumber.NAPLES));
			result.setSelf(CoResult.ok(hymnDtos));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<List<HymnDto>, PersistenceException> getTotalRecords() {
		try {
			final List<HymnDto> hymnDtos = this.hymnRepository.findAll(COMMON_CONDITION, Sort.by(Direction.ASC, "id"))
					.stream()
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
							hymnsRecord.getScore(), null, null, null))
					.toList();
			return CoResult.ok(hymnDtos);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, PersistenceException> infoDeletion(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			val.setVisibleFlg(Boolean.FALSE);
			try {
				this.hymnRepository.saveAndFlush(val);
				result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_DELETED));
			} catch (final PersistenceException e) {
				result.setSelf(CoResult.err(e));
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<Integer, PersistenceException> infoStorage(final HymnDto hymnDto) {
		final Hymn hymnsRecord = new Hymn();
		final HymnsWork hymnsWork = new HymnsWork();
		CoBeanUtils.copyNullableProperties(hymnDto, hymnsRecord);
		final String trimSerif = this.trimSerif(hymnDto.serif());
		hymnsRecord.setId(SnowflakeUtils.snowflakeId());
		hymnsRecord.setSerif(trimSerif);
		hymnsRecord.setVisibleFlg(Boolean.TRUE);
		hymnsRecord.setUpdatedUser(Long.parseLong(hymnDto.updatedUser()));
		hymnsWork.setId(hymnsRecord.getId());
		try {
			this.hymnsWorkRepository.saveAndFlush(hymnsWork);
			this.hymnRepository.saveAndFlush(hymnsRecord);
			final long totalRecords = this.hymnRepository.count(COMMON_CONDITION);
			final int discernedLargestPage = CoProjectUtils.discernLargestPage(totalRecords);
			return CoResult.ok(discernedLargestPage);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, PersistenceException> infoUpdation(final HymnDto hymnDto) {
		final Hymn hymn = new Hymn();
		CoBeanUtils.copyNullableProperties(hymnDto, hymn);
		hymn.setId(Long.parseLong(hymnDto.id()));
		hymn.setVisibleFlg(Boolean.TRUE);
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), hymn.getId());
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			final OffsetDateTime updatedTime1 = val.getUpdatedTime();
			final byte[] score1 = val.getScore();
			final String updatedUser2 = hymnDto.updatedUser();
			val.setScore(null);
			val.setUpdatedUser(null);
			val.setUpdatedTime(null);
			if (CoProjectUtils.isEqual(val, hymn)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				CoBeanUtils.copyNullableProperties(hymn, val);
				final String trimSerif = this.trimSerif(hymn.getSerif());
				val.setSerif(trimSerif);
				val.setScore(score1);
				val.setUpdatedUser(Long.parseLong(updatedUser2));
				val.setUpdatedTime(updatedTime1);
				try {
					this.hymnRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED));
				} catch (final VersionMismatchException e) {
					result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_OPTIMISTIC_ERROR)));
				} catch (final PersistenceException e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	/**
	 * 漢字から片仮名へ転換する
	 *
	 * @param inputText インプットストリング
	 * @return 片仮名
	 */
	private @NotNull String kanjiToKatakana(final @NotNull String inputText) {
		// レギューラーエクスプレスで漢字、平仮名及び片仮名を抽出する
		final String regex1 = "[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}\\p{IsLatin}\\p{Nd}]+";
		final String regex2 = "[\\p{IsKatakana}\\p{IsLatin}\\p{Nd}]+";
		final StringBuilder builder = new StringBuilder();
		final Tokenizer tokenizer = new Tokenizer();
		for (final char ch : inputText.toCharArray()) {
			final String inputChar = String.valueOf(ch);
			if (Pattern.matches(regex1, inputChar)) {
				if (Pattern.matches(regex2, inputChar)) {
					builder.append(inputChar);
				} else {
					final List<Token> tokens = tokenizer.tokenize(inputChar);
					for (final Token token : tokens) {
						builder.append(token.getReading());
					}
				}
			}
		}
		return builder.toString();
	}

	/**
	 * ランドム選択ループ1
	 *
	 * @param hymnsRecords 選択したレコード
	 * @param totalRecords 総合レコード
	 * @return List<Hymn>
	 */
	private @NotNull List<Hymn> randomFiveLoop(final @NotNull List<Hymn> hymnsRecords,
			final @NotNull List<Hymn> totalRecords) {
		final List<Long> ids = hymnsRecords.stream().map(Hymn::getId).distinct().toList();
		final List<Hymn> filteredRecords = totalRecords.stream().filter(item -> !ids.contains(item.getId())).toList();
		final List<Hymn> concernList1 = new ArrayList<>(hymnsRecords);
		if (hymnsRecords.size() < ProjectConstants.DEFAULT_PAGE_SIZE) {
			final int sagaku = ProjectConstants.DEFAULT_PAGE_SIZE - hymnsRecords.size();
			for (int i = 1; i <= sagaku; i++) {
				final int indexOf = RANDOM.nextInt(filteredRecords.size());
				final Hymn hymnsRecord = filteredRecords.get(indexOf);
				concernList1.add(hymnsRecord);
			}
		}
		final List<Hymn> concernList2 = concernList1.stream().distinct().toList();
		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
			return concernList2;
		}
		return this.randomFiveLoop(concernList2, filteredRecords);
	}

	/**
	 * ランドム選択ループ2
	 *
	 * @param hymnsRecords 選択したレコード
	 * @return List<Hymn>
	 */
	private @NotNull List<Hymn> randomFiveLoop2(final List<Hymn> hymnsRecords) {
		final List<Hymn> concernList1 = new ArrayList<>();
		for (int i = 1; i <= ProjectConstants.DEFAULT_PAGE_SIZE; i++) {
			final int indexOf = RANDOM.nextInt(hymnsRecords.size());
			final Hymn hymnsRecord = hymnsRecords.get(indexOf);
			concernList1.add(hymnsRecord);
		}
		final List<Hymn> concernList2 = concernList1.stream().distinct().toList();
		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
			return concernList2;
		}
		return this.randomFiveLoop(concernList2, hymnsRecords);
	}

	@Override
	public @NotNull CoResult<String, PersistenceException> scoreStorage(final byte[] file, final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			if (Arrays.equals(val.getScore(), file)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				try {
					val.setScore(file);
					this.hymnRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED));
				} catch (final PersistenceException e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	/**
	 * テキストによって韓国語単語を取得する
	 *
	 * @param originalText テキスト
	 * @return List<String>
	 */
	private List<String> tokenizeKoreanText(final @NotNull String originalText) {
		final String regex = "\\p{IsHangul}";
		final StringBuilder builder = new StringBuilder();
		for (final char ch : originalText.toCharArray()) {
			if (Pattern.matches(regex, String.valueOf(ch))) {
				builder.append(ch);
			}
		}
		final String koreanText = builder.toString();
		if (CoProjectUtils.isEmpty(koreanText)) {
			return new ArrayList<>();
		}
		final List<kr.co.shineware.nlp.komoran.model.Token> tokenList = KOMORAN.analyze(koreanText).getTokenList();
		return tokenList.stream().map(kr.co.shineware.nlp.komoran.model.Token::getMorph).toList();
	}

	/**
	 * セリフの全半角スペースを削除する
	 *
	 * @param serif セリフ
	 * @return トリムドのセリフ
	 */
	private @NotNull String trimSerif(final @NotNull String serif) {
		final String zenkakuSpace = "\u3000";
		final String replace = serif.replace(zenkakuSpace, CoProjectUtils.EMPTY_STRING);
		final String hankakuSpace = "\u0020";
		return replace.replace(hankakuSpace, CoProjectUtils.EMPTY_STRING);
	}

}
