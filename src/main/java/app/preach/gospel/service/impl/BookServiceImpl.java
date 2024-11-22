package app.preach.gospel.service.impl;

import java.util.List;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.entity.Chapter;
import app.preach.gospel.entity.Phrase;
import app.preach.gospel.repository.BookDao;
import app.preach.gospel.repository.ChapterDao;
import app.preach.gospel.repository.PhraseDao;
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
	 * 共通リポジトリ
	 */
	private final Jdbi jdbi;

	@Override
	public CoResult<List<BookDto>, JdbiException> getBooks() {
		try {
			final List<BookDto> bookDtos = this.jdbi.onDemand(BookDao.class).findAll().stream()
					.map(item -> new BookDto(item.getId(), item.getName(), item.getNameJp())).toList();
			return CoResult.ok(bookDtos);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<List<ChapterDto>, JdbiException> getChaptersByBookId(final String id) {
		try {
			List<ChapterDto> chapterDtos;
			if (CoProjectUtils.isDigital(id)) {
				chapterDtos = this.jdbi.onDemand(ChapterDao.class).findByBookId(Short.parseShort(id)).stream()
						.map(item -> new ChapterDto(item.getId(), item.getName(), item.getNameJp(), item.getBookId()))
						.toList();
			} else {
				chapterDtos = this.jdbi.onDemand(ChapterDao.class).findByBookId((short) 1).stream()
						.map(item -> new ChapterDto(item.getId(), item.getName(), item.getNameJp(), item.getBookId()))
						.toList();
			}
			return CoResult.ok(chapterDtos);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<String, JdbiException> infoStorage(final @NotNull PhraseDto phraseDto) {
		final long id = Long.parseLong(phraseDto.id());
		final Integer chapterId = phraseDto.chapterId();
		try {
			final Chapter chapter = this.jdbi.onDemand(ChapterDao.class).selectById(chapterId);
			final Phrase phrase = new Phrase();
			CoBeanUtils.copyNullableProperties(phraseDto, phrase);
			phrase.setId((chapterId * 1000) + id);
			phrase.setName(chapter.getName().concat("\u003a").concat(phraseDto.id()));
			phrase.setChapterId(chapterId);
			phrase.setChangeLine(Boolean.FALSE);
			this.jdbi.onDemand(PhraseDao.class).insertOne(phrase);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_INSERTED);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

}
