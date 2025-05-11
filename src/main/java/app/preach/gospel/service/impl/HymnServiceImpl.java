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

import org.apache.tika.Tika;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.VersionMismatchException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 賛美歌サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Log4j2
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HymnServiceImpl implements IHymnService {

	/**
	 * 共通検索条件
	 */
	private static final Specification<Hymn> COMMON_CONDITION = (root, query, criteriaBuilder) -> criteriaBuilder
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
	 * 日本語名称(別)
	 */
	private static final String NAME_JP_RA = "nameJpRa";

	/**
	 * 日本語名称(別)
	 */
	private static final String HYMNS_WORK = "hymnsWork";

	/**
	 * 怪しいキーワードリスト
	 */
	private static final String[] STRANGE_ARRAY = { "insert", "delete", "update", "create", "drop", "#", "$", "%", "&",
			"(", ")", "\"", "'", "@", ":", "select" };

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
		if (normA == 0 || normB == 0) {
			return 0; // 避免除0
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	/**
	 * 通常検索条件を取得する
	 *
	 * @param keyword キーワード
	 * @return Specification<Hymn>
	 */
	private static @NotNull Specification<Hymn> getHymnSpecification(final String keyword) {
		final String searchStr = CoProjectUtils.isEmpty(keyword) ? CoProjectUtils.HANKAKU_PERCENTSIGN
				: CoProjectUtils.HANKAKU_PERCENTSIGN.concat(keyword).concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
		return (root, query, criteriaBuilder) -> {
			final Join<Hymn, HymnsWork> hymnsJoin = root.join(HYMNS_WORK, JoinType.INNER);
			return criteriaBuilder.or(criteriaBuilder.like(root.get(NAME_JP), searchStr),
					criteriaBuilder.like(root.get(NAME_KR), searchStr),
					criteriaBuilder.like(hymnsJoin.get(NAME_JP_RA), searchStr));
		};
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
	public CoResult<Integer, Exception> checkDuplicated(final String id, final String nameJp) {
		try {
			final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(NAME_JP), nameJp);
			if (CoProjectUtils.isDigital(id)) {
				final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
						.notEqual(root.get("id"), Long.valueOf(id));
				final long duplicated = this.hymnRepository
						.count(COMMON_CONDITION.and(specification).and(specification1));
				return CoResult.ok((int) duplicated);
			}
			final long duplicated = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			return CoResult.ok((int) duplicated);
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<Integer, Exception> checkDuplicated2(final String id, final String nameKr) {
		try {
			final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(NAME_KR), nameKr);
			if (CoProjectUtils.isDigital(id)) {
				final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
						.notEqual(root.get("id"), Long.valueOf(id));
				final long duplicated = this.hymnRepository
						.count(COMMON_CONDITION.and(specification).and(specification1));
				return CoResult.ok((int) duplicated);
			}
			final long duplicated = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			return CoResult.ok((int) duplicated);
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	/**
	 * TF-IDFベクターを計算する
	 *
	 * @param originalText 生のストリング
	 * @return double[]
	 */
	private double @NotNull [] computeTFIDFVector(final String originalText) {
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
	private List<Hymn> findTopTwoMatches(final String target, final @NotNull List<Hymn> elements) {
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
	public @NotNull CoResult<HymnDto, Exception> getHymnInfoById(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<HymnDto, Exception> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			final Specification<Student> specification1 = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), val.getUpdatedUser()));
			};
			this.studentRepository.findOne(specification1).ifPresentOrElse(subVal -> {
				final Specification<HymnsWork> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
						.equal(root.get("workId"), val.getId());
				final HymnsWork hymnsWork = this.hymnsWorkRepository.findOne(specification2)
						.orElseThrow(() -> new HibernateException(ProjectConstants.MESSAGE_HYMNSWORK_NOT_FOUND));
				final ZonedDateTime zonedDateTime = val.getUpdatedTime().atZoneSameInstant(ZoneOffset.ofHours(9));
				final HymnDto hymnDto = new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(),
						val.getSerif(), val.getLink(), hymnsWork.getScore(), hymnsWork.getBiko(), subVal.getUsername(),
						FORMATTER.format(zonedDateTime.toLocalDateTime()), null);
				result.setSelf(CoResult.ok(hymnDto));
			}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STUDENT_NOT_FOUND))));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_HYMN_NOT_FOUND))));
		return result;
	}

	@Override
	public CoResult<Pagination<HymnDto>, Exception> getHymnsByKeyword(final Integer pageNum, final String keyword) {
		final Specification<Hymn> specification = getHymnSpecification(keyword);
		try {
			final long totalRecords = this.hymnRepository.count(COMMON_CONDITION.and(specification));
			final PageRequest pageRequest = PageRequest.of(pageNum - 1, ProjectConstants.DEFAULT_PAGE_SIZE,
					Sort.by(Direction.ASC, "id"));
			final Page<Hymn> hymnsRecords = this.hymnRepository.findAll(COMMON_CONDITION.and(specification),
					pageRequest);
			final List<HymnDto> hymnDtos = this.mapToDtos(hymnsRecords.getContent(), LineNumber.SNOWY);
			return CoResult.ok(Pagination.of(hymnDtos, totalRecords, pageNum, ProjectConstants.DEFAULT_PAGE_SIZE));
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<List<HymnDto>, Exception> getHymnsRandomFive(final String keyword) {
		try {
			for (final String starngement : STRANGE_ARRAY) {
				if (keyword.toLowerCase().contains(starngement) || keyword.length() >= 100) {
					final List<HymnDto> hymnDtos = this.mapToDtos(this.hymnRepository.findForStrangement(),
							LineNumber.SNOWY);
					log.error("怪しいキーワード： {}", keyword);
					return CoResult.ok(hymnDtos);
				}
			}
			if (CoProjectUtils.isEmpty(keyword)) {
				final List<HymnDto> totalRecords = this.mapToDtos(this.hymnRepository.findAll(COMMON_CONDITION),
						LineNumber.SNOWY);
				final List<HymnDto> hymnDtos1 = this.randomFiveLoop2(totalRecords);
				return CoResult.ok(hymnDtos1);
			}
			final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> {
				final Join<Hymn, HymnsWork> hymnsJoin = root.join(HYMNS_WORK, JoinType.INNER);
				return criteriaBuilder.or(criteriaBuilder.equal(root.get(NAME_JP), keyword),
						criteriaBuilder.equal(root.get(NAME_KR), keyword),
						criteriaBuilder.like(hymnsJoin.get(NAME_JP_RA), "%[".concat(keyword).concat("]%")));
			};
			final List<HymnDto> withName = this.mapToDtos(this.hymnRepository.findAll(specification1),
					LineNumber.CADMIUM);
			final List<HymnDto> hymnDtos = new ArrayList<>(withName);
			final List<String> withNameIds = withName.stream().map(HymnDto::id).toList();
			final Specification<Hymn> specification2 = getHymnSpecification(keyword);
			final List<HymnDto> withNameLike = this.mapToDtos(this.hymnRepository.findAll(specification2).stream()
					.filter(a -> !withNameIds.contains(a.getId().toString())).toList(), LineNumber.BURGUNDY);
			hymnDtos.addAll(withNameLike);
			final List<String> withNameLikeIds = withNameLike.stream().map(HymnDto::id).toList();
			if (hymnDtos.stream().distinct().toList().size() >= ProjectConstants.DEFAULT_PAGE_SIZE) {
				final List<HymnDto> randomFiveLoop = this.randomFiveLoop(withName, withNameLike);
				return CoResult.ok(randomFiveLoop.stream()
						.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList());
			}
			final String detailKeyword = CoProjectUtils.getDetailKeyword(keyword);
			final List<HymnDto> withRandomFive = this
					.mapToDtos(
							this.hymnRepository.retrieveRandomFive(detailKeyword).stream()
									.filter(a -> !withNameIds.contains(a.getId().toString())
											&& !withNameLikeIds.contains(a.getId().toString()))
									.toList(),
							LineNumber.NAPLES);
			hymnDtos.addAll(withRandomFive);
			if (hymnDtos.stream().distinct().toList().size() >= ProjectConstants.DEFAULT_PAGE_SIZE) {
				final List<HymnDto> hymnDtos2 = new ArrayList<>();
				hymnDtos2.addAll(withName);
				hymnDtos2.addAll(withNameLike);
				final List<HymnDto> randomFiveLoop = this.randomFiveLoop(hymnDtos2, withRandomFive);
				return CoResult.ok(randomFiveLoop.stream()
						.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList());
			}
			final List<HymnDto> totalRecords = this.mapToDtos(this.hymnRepository.findAll(COMMON_CONDITION),
					LineNumber.SNOWY);
			final List<HymnDto> randomFiveLoop = this.randomFiveLoop(hymnDtos, totalRecords);
			return CoResult.ok(randomFiveLoop.stream()
					.sorted(Comparator.comparingInt(item -> item.lineNumber().getLineNo())).toList());
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<List<HymnDto>, Exception> getKanumiList(final Long id) {
		final Specification<Hymn> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<List<HymnDto>, Exception> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification1)).ifPresentOrElse(val -> {
			final List<HymnDto> hymnDtos = new ArrayList<>();
			hymnDtos.add(new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(), val.getSerif(),
					val.getLink(), null, null, null, null, LineNumber.BURGUNDY));
			final Specification<Hymn> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
					.notEqual(root.get("id"), id);
			final List<Hymn> hymns = this.hymnRepository.findAll(COMMON_CONDITION.and(specification2));
			final List<Hymn> topTwoMatches = this.findTopTwoMatches(val.getSerif(), hymns);
			final List<HymnDto> list = this.mapToDtos(topTwoMatches, LineNumber.NAPLES);
			hymnDtos.addAll(list);
			result.setSelf(CoResult.ok(hymnDtos));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_HYMN_NOT_FOUND))));
		return result;
	}

	@Override
	public CoResult<Long, Exception> getTotalCounts() {
		try {
			final long count = this.hymnRepository.count(COMMON_CONDITION);
			return CoResult.ok(count);
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, Exception> infoDeletion(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<String, Exception> result = CoResult.getInstance();
		this.hymnRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			val.setVisibleFlg(Boolean.FALSE);
			try {
				this.hymnRepository.saveAndFlush(val);
				result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_DELETED));
			} catch (final Exception e) {
				result.setSelf(CoResult.err(e));
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_HYMN_NOT_FOUND))));
		return result;
	}

	@Override
	public CoResult<Integer, Exception> infoStorage(final HymnDto hymnDto) {
		final Hymn hymnsRecord = new Hymn();
		final HymnsWork hymnsWork = new HymnsWork();
		CoBeanUtils.copyNullableProperties(hymnDto, hymnsRecord);
		final String trimSerif = this.trimSerif(hymnDto.serif());
		hymnsRecord.setId(SnowflakeUtils.snowflakeId());
		hymnsRecord.setSerif(trimSerif);
		hymnsRecord.setVisibleFlg(Boolean.TRUE);
		hymnsRecord.setUpdatedUser(Long.valueOf(hymnDto.updatedUser()));
		hymnsWork.setWorkId(hymnsRecord.getId());
		try {
			this.hymnRepository.saveAndFlush(hymnsRecord);
			this.hymnsWorkRepository.saveAndFlush(hymnsWork);
			final long totalRecords = this.hymnRepository.count(COMMON_CONDITION);
			final int discernedLargestPage = CoProjectUtils.discernLargestPage(totalRecords);
			return CoResult.ok(discernedLargestPage);
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, Exception> infoUpdation(final HymnDto hymnDto) {
		final Hymn hymn = new Hymn();
		CoBeanUtils.copyNullableProperties(hymnDto, hymn);
		hymn.setId(Long.valueOf(hymnDto.id()));
		hymn.setVisibleFlg(Boolean.TRUE);
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), hymn.getId());
		final CoResult<String, Exception> result = CoResult.getInstance();
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
				val.setUpdatedUser(Long.valueOf(updatedUser2));
				val.setUpdatedTime(updatedTime1);
				try {
					this.hymnRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED));
				} catch (final VersionMismatchException e) {
					result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_OPTIMISTIC_ERROR)));
				} catch (final Exception e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_HYMN_NOT_FOUND))));
		return result;
	}

	/**
	 * DTOへ変換する
	 *
	 * @param hymns      賛美歌リスト
	 * @param lineNumber 行番号
	 * @return List<HymnDto>
	 */
	private List<HymnDto> mapToDtos(final @NotNull List<Hymn> hymns, final LineNumber lineNumber) {
		return hymns.stream()
				.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
						hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(), null, null, null, null,
						lineNumber))
				.toList();
	}

	/**
	 * コーパスを取得する
	 *
	 * @param originalTexts テキスト
	 */
	private void preprocessCorpus(final @NotNull List<String> originalTexts) {
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
	 * @return List<HymnDto>
	 */
	private @NotNull List<HymnDto> randomFiveLoop(final @NotNull List<HymnDto> hymnsRecords,
			final @NotNull List<HymnDto> totalRecords) {
		final List<String> ids = hymnsRecords.stream().map(HymnDto::id).distinct().toList();
		final List<HymnDto> filteredRecords = totalRecords.stream().filter(item -> !ids.contains(item.id())).toList();
		final List<HymnDto> concernList1 = new ArrayList<>(hymnsRecords);
		if (hymnsRecords.size() < ProjectConstants.DEFAULT_PAGE_SIZE) {
			final int sagaku = ProjectConstants.DEFAULT_PAGE_SIZE - hymnsRecords.size();
			for (int i = 1; i <= sagaku; i++) {
				final int indexOf = RANDOM.nextInt(filteredRecords.size());
				final HymnDto hymnsRecord = filteredRecords.get(indexOf);
				concernList1.add(hymnsRecord);
			}
		}
		final List<HymnDto> concernList2 = concernList1.stream().distinct().toList();
		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
			return concernList2;
		}
		return this.randomFiveLoop(concernList2, filteredRecords);
	}

	/**
	 * ランドム選択ループ2
	 *
	 * @param hymnsRecords 選択したレコード
	 * @return List<HymnDto>
	 */
	private @NotNull List<HymnDto> randomFiveLoop2(final List<HymnDto> hymnsRecords) {
		final List<HymnDto> concernList1 = new ArrayList<>();
		for (int i = 1; i <= ProjectConstants.DEFAULT_PAGE_SIZE; i++) {
			final int indexOf = RANDOM.nextInt(hymnsRecords.size());
			final HymnDto hymnsRecord = hymnsRecords.get(indexOf);
			concernList1.add(hymnsRecord);
		}
		final List<HymnDto> concernList2 = concernList1.stream().distinct().toList();
		if (concernList2.size() == ProjectConstants.DEFAULT_PAGE_SIZE) {
			return concernList2;
		}
		return this.randomFiveLoop(concernList2, hymnsRecords);
	}

	@Override
	public @NotNull CoResult<String, Exception> scoreStorage(final byte[] file, final Long id) {
		final CoResult<String, Exception> result = CoResult.getInstance();
		this.hymnsWorkRepository.findById(id).ifPresentOrElse(val -> {
			if (Arrays.equals(val.getScore(), file)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				final Tika tika = new Tika();
				final String pdfDiscernment = tika.detect(file);
				val.setBiko(pdfDiscernment);
				try {
					val.setScore(file);
					this.hymnsWorkRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED));
				} catch (final Exception e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_HYMNSWORK_NOT_FOUND))));
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
		final List<Token> tokenList = KOMORAN.analyze(koreanText).getTokenList();
		return tokenList.stream().filter(token -> token.getPos().startsWith("NN"))
				.collect(Collectors.toMap(Token::getMorph, t -> 1, Integer::sum));
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
		return replace.trim();
	}

}
