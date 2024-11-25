package app.preach.gospel.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import app.preach.gospel.utils.Pagination;
import app.preach.gospel.utils.SnowflakeUtils;
import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 賛美歌サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HymnServiceImpl implements IHymnService {

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * ランドム選択
	 */
	private static final Random RANDOM = new Random();

	/**
	 * ドルマーク
	 */
	private static final String DOLLAR_MARK = "\u0024";

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
		Specification<Hymn> specification;
		if (CoProjectUtils.isDigital(id)) {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.notEqual(root.get("id"), Long.parseLong(id)),
						criteriaBuilder.equal(root.get("nameJp"), nameJp));
			};
		} else {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("nameJp"), nameJp));
			};
		}
		try {
			final long duplicated = this.hymnRepository.count(specification);
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<Integer, PersistenceException> checkDuplicated2(final String id, final String nameKr) {
		Specification<Hymn> specification;
		if (CoProjectUtils.isDigital(id)) {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.notEqual(root.get("id"), Long.parseLong(id)),
						criteriaBuilder.equal(root.get("nameKr"), nameKr));
			};
		} else {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("nameKr"), nameKr));
			};
		}
		try {
			final long duplicated = this.hymnRepository.count(specification);
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<HymnDto, PersistenceException> getHymnInfoById(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), id));
		};
		final CoResult<HymnDto, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(specification).ifPresentOrElse(val -> {
			final Specification<Student> specification2 = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), val.getUpdatedUser()));
			};
			this.studentRepository.findOne(specification2).ifPresentOrElse(subVal -> {
				final ZonedDateTime zonedDateTime = val.getUpdatedTime().atZoneSameInstant(ZoneOffset.ofHours(9));
				final HymnDto hymnDto = new HymnDto(val.getId().toString(), val.getNameJp(), val.getNameKr(),
						val.getSerif(), val.getLink(), val.getScore(), subVal.getUsername(),
						FORMATTER.format(zonedDateTime.toLocalDateTime()), null);
				result.setSelf(CoResult.ok(hymnDto));
			}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<Pagination<HymnDto>, PersistenceException> getHymnsByKeyword(final Integer pageNum,
			final String keyword) {
		final String searchStr = CoProjectUtils.getDetailKeyword(keyword);
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.or(criteriaBuilder.like(root.get("nameJp"), searchStr),
					criteriaBuilder.like(root.get("nameKr"), searchStr));
		};
		try {
			final long totalRecords = this.hymnRepository.count(specification);
			final PageRequest pageRequest = PageRequest.of(pageNum - 1, ProjectConstants.DEFAULT_PAGE_SIZE,
					Sort.by(Direction.ASC, "id"));
			final Page<Hymn> hymnsRecords = this.hymnRepository.findAll(specification, pageRequest);
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
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("visibleFlg"), Boolean.TRUE);
		try {
			final List<Hymn> totalRecords = this.hymnRepository.findAll(specification);
			final List<HymnsWork> hymnsWorks = totalRecords.stream().map(item -> {
				final HymnsWork hymnsWork = this.hymnsWorkRepository.findById(item.getId()).orElseGet(HymnsWork::new);
				if ((hymnsWork.getUpdatedTime() == null) || item.getUpdatedTime().isAfter(hymnsWork.getUpdatedTime())) {
					final String serif = item.getSerif();
					final String toKatakanaSerif = this.kanjiToKatakana(serif);
					hymnsWork.setId(item.getId());
					hymnsWork.setSerif(toKatakanaSerif);
					hymnsWork.setUpdatedTime(OffsetDateTime.now());
					return hymnsWork;
				}
				return null;
			}).filter(Objects::nonNull).toList();
			this.hymnsWorkRepository.saveAllAndFlush(hymnsWorks);
			String searchStr;
			String searchKatakana;
			final String kanjiToKatakanaKeyword = this.kanjiToKatakana(keyword);
			if (CoProjectUtils.isNotEmpty(keyword) && keyword.endsWith(DOLLAR_MARK)) {
				searchStr = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(keyword)
						.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
				searchKatakana = CoProjectUtils.HANKAKU_PERCENTSIGN.concat(kanjiToKatakanaKeyword)
						.concat(CoProjectUtils.HANKAKU_PERCENTSIGN);
			} else {
				searchStr = CoProjectUtils.getDetailKeyword(keyword);
				searchKatakana = CoProjectUtils.getDetailKeyword(kanjiToKatakanaKeyword);
			}
			final List<Hymn> hymns = this.hymnRepository.retrieveRandomFive(searchStr, searchKatakana);
			if (CollectionUtils.isEmpty(hymns) || (hymns.size() <= ProjectConstants.DEFAULT_PAGE_SIZE)) {
				final List<Hymn> randomFiveLoop = this.randomFiveLoop(hymns, totalRecords);
				final List<HymnDto> hymnDtos = randomFiveLoop.stream()
						.map(hymnsRecord -> new HymnDto(hymnsRecord.getId().toString(), hymnsRecord.getNameJp(),
								hymnsRecord.getNameKr(), hymnsRecord.getSerif(), hymnsRecord.getLink(),
								hymnsRecord.getScore(), null, null, null))
						.toList();
				return CoResult.ok(hymnDtos);
			}
			final List<HymnDto> hymnDtos = this.randomFiveLoop2(hymns).stream()
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
	public CoResult<Long, PersistenceException> getTotalRecords() {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("visibleFlg"), Boolean.TRUE);
		try {
			final long totalRecords = this.hymnRepository.count(specification);
			return CoResult.ok(totalRecords);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<String, PersistenceException> infoDeletion(final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), id));
		};
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(specification).ifPresentOrElse(val -> {
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
			final Specification<Hymn> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get("visibleFlg"), Boolean.TRUE);
			final long totalRecords = this.hymnRepository.count(specification);
			final int discernedLargestPage = CoProjectUtils.discernLargestPage(totalRecords);
			return CoResult.ok(discernedLargestPage);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<String, PersistenceException> infoUpdation(final HymnDto hymnDto) {
		final Hymn hymnsRecord = new Hymn();
		CoBeanUtils.copyNullableProperties(hymnDto, hymnsRecord);
		hymnsRecord.setId(Long.parseLong(hymnDto.id()));
		hymnsRecord.setVisibleFlg(Boolean.TRUE);
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), hymnsRecord.getId()));
		};
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(specification).ifPresentOrElse(val -> {
			final OffsetDateTime updatedTime = val.getUpdatedTime();
			val.setScore(null);
			val.setUpdatedUser(null);
			val.setUpdatedTime(null);
			if (CoProjectUtils.isEqual(val, hymnsRecord)) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				CoBeanUtils.copyNullableProperties(hymnsRecord, val);
				final String trimSerif = this.trimSerif(hymnsRecord.getSerif());
				val.setSerif(trimSerif);
				val.setUpdatedTime(updatedTime);
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
	private @NotNull List<Hymn> randomFiveLoop(final List<Hymn> hymnsRecords, final @NotNull List<Hymn> totalRecords) {
		final List<Hymn> concernList1 = new ArrayList<>();
		final List<Long> ids = hymnsRecords.stream().map(Hymn::getId).toList();
		final List<Hymn> filteredRecords = totalRecords.stream().filter(item -> !ids.contains(item.getId())).toList();
		concernList1.addAll(hymnsRecords);
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
	public CoResult<String, PersistenceException> scoreStorage(final byte[] file, final Long id) {
		final Specification<Hymn> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), id));
		};
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.hymnRepository.findOne(specification).ifPresentOrElse(val -> {
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
	 * セリフの全半角スペースを削除する
	 *
	 * @param serif セリフ
	 * @return トリムドのセリフ
	 */
	private String trimSerif(final String serif) {
		final String zenkakuSpace = "\u3000";
		final String replace = serif.replace(zenkakuSpace, CoProjectUtils.EMPTY_STRING);
		final String hankakuSpace = "\u0020";
		return replace.replace(hankakuSpace, CoProjectUtils.EMPTY_STRING);
	}

}
