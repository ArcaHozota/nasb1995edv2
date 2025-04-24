package app.preach.gospel.service.impl;

import java.util.Comparator;
import java.util.List;

import org.hibernate.HibernateException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.entity.Chapter;
import app.preach.gospel.entity.Phrase;
import app.preach.gospel.repository.BookRepository;
import app.preach.gospel.repository.ChapterRepository;
import app.preach.gospel.repository.PhraseRepository;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoBeanUtils;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 聖書章節サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookServiceImpl implements IBookService {

	/**
	 * 聖書書別リポジトリ
	 */
	private final BookRepository bookRepository;

	/**
	 * 聖書章節リポジトリ
	 */
	private final ChapterRepository chapterRepository;

	/**
	 * 聖書節別リポジトリ
	 */
	private final PhraseRepository phraseRepository;

	@Override
	public CoResult<List<BookDto>, Exception> getBooks() {
		final Sort sort = Sort.by(Direction.ASC, "id");
		try {
			final List<BookDto> bookDtos = this.bookRepository.findAll(sort).stream()
					.map(item -> new BookDto(item.getId().toString(), item.getName(), item.getNameJp())).toList();
			return CoResult.ok(bookDtos);
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<List<ChapterDto>, Exception> getChaptersByBookId(final String id) {
		try {
			final CoResult<List<ChapterDto>, Exception> result = CoResult.getInstance();
			this.bookRepository
					.findByIdWithChapters(CoProjectUtils.isDigital(id) ? Short.parseShort(id) : Short.parseShort("1"))
					.ifPresentOrElse(val -> {
						final List<ChapterDto> chapterDtos = val.getChapters().stream()
								.sorted(Comparator.comparingInt(Chapter::getId))
								.map(item -> new ChapterDto(item.getId().toString(), item.getName(), item.getNameJp(),
										item.getBookId().toString()))
								.toList();
						result.setSelf(CoResult.ok(chapterDtos));
					}, () -> result
							.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_BOOK_NOT_FOUND))));
			return result;
		} catch (final Exception e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, Exception> infoStorage(final @NotNull PhraseDto phraseDto) {
		final long id = Long.parseLong(phraseDto.id());
		final Integer chapterId = Integer.parseInt(phraseDto.chapterId());
		final CoResult<String, Exception> result = CoResult.getInstance();
		this.chapterRepository.findById(chapterId).ifPresentOrElse(val -> {
			final Phrase phrase = new Phrase();
			CoBeanUtils.copyNullableProperties(phraseDto, phrase);
			phrase.setId(chapterId * 1000 + id);
			phrase.setName(val.getName().concat(CoProjectUtils.HANKAKU_COLON).concat(phraseDto.id()));
			phrase.setChapterId(chapterId);
			final String textEn = phrase.getTextEn();
			if (textEn.endsWith(CoProjectUtils.HANKAKU_SHARP)) {
				phrase.setChangeLine(Boolean.TRUE);
			} else {
				phrase.setChangeLine(Boolean.FALSE);
			}
			try {
				this.phraseRepository.saveAndFlush(phrase);
				result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_BOOKS));
			} catch (final Exception e) {
				result.setSelf(CoResult.err(e));
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_CHAPTER_NOT_FOUND))));
		return result;
	}

}
