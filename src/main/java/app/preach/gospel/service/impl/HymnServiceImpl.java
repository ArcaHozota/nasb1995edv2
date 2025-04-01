package app.preach.gospel.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	/**
	 * 計算マップ1
	 */
	private final Map<String, Integer> termToIndex = new LinkedHashMap<>();

	/**
	 * 計算マップ2
	 */
	private final Map<String, Integer> docFreq = new LinkedHashMap<>();

	/**
	 * コーパスサイズ
	 */
	private int corpusSize;

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
	 * TF-IDFベクターを計算する
	 *
	 * @param originalText 生のストリング
	 * @return double[]
	 */
	private double[] computeTFIDFVector(final String originalText) {
		final Map<String, Integer> termFreq = this.tokenizeKoreanTextWithFrequency(originalText);
		final int totalTerms = termFreq.values().stream().mapToInt(Integer::intValue).sum();
		final double[] vector = new double[this.termToIndex.size()];
		Arrays.fill(vector, 0.00);
		termFreq.forEach((term, count) -> {
			if (this.termToIndex.containsKey(term)) {
				final int index = this.termToIndex.get(term);
				// 计算TF
				final double tf = (double) count / totalTerms;
				// 计算IDF
				final int df = this.docFreq.getOrDefault(term, 0);
				final double idf = Math.log((double) this.corpusSize / (df + 1));
				vector[index] = tf * idf;
			}
		});
		return vector;
	}

	/**
	 * 最も似てる三つの賛美歌を取得する
	 *
	 * @param target   目標テキスト
	 * @param elements 賛美歌リスト
	 * @return List<Hymn>
	 */
	private List<Hymn> findTopTwoMatches(final String target, final List<Hymn> elements) {
		final List<String> texts = elements.stream().map(Hymn::getSerif).toList();
		this.preprocessCorpus(texts);
		final double[] targetVector = this.computeTFIDFVector(target);
		final List<double[]> elementVectors = elements.stream().map(item -> this.computeTFIDFVector(item.getSerif()))
				.toList();
		final PriorityQueue<Entry<Hymn, Double>> maxHeap = new PriorityQueue<>(
				Comparator.comparing(Entry<Hymn, Double>::getValue).reversed());
		for (int i = 0; i < elements.size(); i++) {
			final double similarity = HymnServiceImpl.cosineSimilarity(targetVector, elementVectors.get(i));
			maxHeap.add(new AbstractMap.SimpleEntry<>(elements.get(i), similarity));
		}
		return maxHeap.stream().limit(3).map(Entry::getKey).toList();
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
						final HymnsWork hymnsWork = this.hymnsWorkRepository.findById(val.getId())
								.orElseGet(HymnsWork::new);
						final ZonedDateTime zonedDateTime = val.getUpdatedTime()
								.atZoneSameInstant(ZoneOffset.ofHours(9));
						final HymnDto hymnDto = new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(),
								val.getSerif(), val.getLink(), hymnsWork.getScore(), subVal.getUsername(),
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
		final String searchStr = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(keyword)
				.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
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
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null,
							null))
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
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
								null, LineNumber.SNOWY))
						.toList();
				return CoResult.ok(hymnDtos);
			}
			for (final String starngement : STRANGE_ARRAY) {
				if (keyword.toLowerCase().contains(starngement) || (keyword.length() >= 100)) {
					final List<HymnDto> hymnDtos = this.hymnRepository.findForStrangement().stream()
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
									null, LineNumber.SNOWY))
							.toList();
					log.error("怪しいキーワード： " + keyword);
					return CoResult.ok(hymnDtos);
				}
			}
			final String searchStr1 = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(keyword)
					.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
			final List<Hymn> hymnsTitle = this.hymnRepository.retrieveRandomFive1(searchStr1);
			final List<HymnDto> hymnDtos = new ArrayList<>();
			final List<HymnDto> hymnDtos0 = hymnsTitle.stream()
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null,
							LineNumber.CADIMIUM))
					.toList();
			hymnDtos.addAll(hymnDtos0);
			final List<Hymn> hymns1 = this.hymnRepository.retrieveRandomFive2(searchStr1);
			final List<Long> titleIds = hymnsTitle.stream().map(Hymn::getId).toList();
			if (CollectionUtils.isEmpty(hymns1) || (hymns1.size() <= ProjectConstants.DEFAULT_PAGE_SIZE)) {
				final List<Hymn> hymns = new ArrayList<>();
				hymns.addAll(hymns1);
				final String searchStr2 = CoProjectUtils.getDetailKeyword(keyword);
				final List<Hymn> hymns2 = this.hymnRepository.retrieveRandomFive2(searchStr2);
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
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
									null, LineNumber.BUNRGUNDY))
							.toList();
					hymnDtos.addAll(hymnDtos1);
					final List<HymnDto> hymnDtos2 = randomFiveLoop.stream().filter(
							a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId()) && ids2.contains(a.getId()))
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
									null, LineNumber.NAPLES))
							.toList();
					hymnDtos.addAll(hymnDtos2);
					final List<HymnDto> hymnDtos3 = randomFiveLoop.stream()
							.filter(a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId())
									&& !ids2.contains(a.getId()))
							.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
									hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
									null, LineNumber.SNOWY))
							.toList();
					hymnDtos.addAll(hymnDtos3);
					return CoResult.ok(hymnDtos.subList(0, 5));
				}
				final List<Hymn> randomFiveLoop = this.randomFiveLoop2(hymns3);
				final List<HymnDto> hymnDtos1 = randomFiveLoop.stream()
						.filter(a -> !titleIds.contains(a.getId()) && ids1.contains(a.getId()))
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
								null, LineNumber.BUNRGUNDY))
						.toList();
				hymnDtos.addAll(hymnDtos1);
				final List<HymnDto> hymnDtos2 = randomFiveLoop.stream()
						.filter(a -> !titleIds.contains(a.getId()) && !ids1.contains(a.getId()))
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null,
								null, LineNumber.NAPLES))
						.toList();
				hymnDtos.addAll(hymnDtos2);
				return CoResult.ok(hymnDtos.subList(0, 5));
			}
			final List<Hymn> randomFiveLoop2 = this.randomFiveLoop2(hymns1);
			final List<HymnDto> hymnDtos1 = randomFiveLoop2.stream().filter(a -> !titleIds.contains(a.getId()))
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null,
							LineNumber.BUNRGUNDY))
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
					val.getLink(), null, null, null, LineNumber.BUNRGUNDY));
			final Specification<Hymn> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
					.notEqual(root.get("id"), id);
			final List<Hymn> hymns = this.hymnRepository.findAll(COMMON_CONDITION.and(specification2));
			final List<Hymn> topTwoMatches = this.findTopTwoMatches(val.getSerif(), hymns);
			final List<HymnDto> list = topTwoMatches.stream()
					.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null,
							LineNumber.NAPLES))
					.toList();
			hymnDtos.addAll(list);
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
							hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null,
							null))
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
		hymnsWork.setWorkId(hymnsRecord.getId());
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
			final String updatedUser2 = hymnDto.updatedUser();
			val.setUpdatedUser(null);
			val.setUpdatedTime(null);
			if (CoProjectUtils.isEqual(val, hymn)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				CoBeanUtils.copyNullableProperties(hymn, val);
				final String trimSerif = this.trimSerif(hymn.getSerif());
				val.setSerif(trimSerif);
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
			if (!Pattern.matches(regex1, inputChar) || Pattern.matches(regex2, inputChar)) {
				builder.append(inputChar);
			} else {
				final List<Token> tokens = tokenizer.tokenize(inputChar);
				for (final Token token : tokens) {
					builder.append(token.getReading());
				}
			}
		}
		return builder.toString();
	}

	/**
	 * コーパスを取得する
	 *
	 * @param originalTexts
	 */
	private void preprocessCorpus(final List<String> originalTexts) {
		this.termToIndex.clear();
		this.docFreq.clear();
		this.corpusSize = originalTexts.size();
		int index = 0;
		// 第一遍：建立文档频率
		for (final String doc : originalTexts) {
			final Map<String, Integer> termFreq = this.tokenizeKoreanTextWithFrequency(doc);
			termFreq.keySet().forEach(term -> this.docFreq.put(term, this.docFreq.getOrDefault(term, 0) + 1));
		}
		// 第二遍：建立词汇表索引
		for (final String term : this.docFreq.keySet()) {
			this.termToIndex.put(term, index++);
		}
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
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnsWorkRepository.findById(id).ifPresentOrElse(val -> {
			if (Arrays.equals(val.getScore(), file)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				try {
					val.setScore(file);
					this.hymnsWorkRepository.saveAndFlush(val);
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
	 * @return Map<String, Integer>
	 */
	private Map<String, Integer> tokenizeKoreanTextWithFrequency(final @NotNull String originalText) {
		final String regex = "\\p{IsHangul}";
		final StringBuilder builder = new StringBuilder();
		for (final char ch : originalText.toCharArray()) {
			if (Pattern.matches(regex, String.valueOf(ch))) {
				builder.append(ch);
			}
		}
		final String koreanText = builder.toString();
		if (CoProjectUtils.isEmpty(koreanText)) {
			return new LinkedHashMap<>();
		}
		final List<kr.co.shineware.nlp.komoran.model.Token> tokenList = KOMORAN.analyze(koreanText).getTokenList();
		return tokenList.stream()
				.collect(Collectors.toMap(kr.co.shineware.nlp.komoran.model.Token::getMorph, t -> 1, Integer::sum));
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
