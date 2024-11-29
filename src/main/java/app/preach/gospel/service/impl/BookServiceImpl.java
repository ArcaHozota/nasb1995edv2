package app.preach.gospel.service.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
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
import jakarta.persistence.PersistenceException;
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
	public CoResult<List<BookDto>, PersistenceException> getBooks() {
		final Sort sort = Sort.by(Direction.ASC, "id");
		try {
			final List<BookDto> bookDtos = this.bookRepository.findAll(sort).stream()
					.map(item -> new BookDto(item.getId(), item.getName(), item.getNameJp())).toList();
			return CoResult.ok(bookDtos);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<List<ChapterDto>, PersistenceException> getChaptersByBookId(final String id) {
		final Sort sort = Sort.by(Direction.ASC, "id");
		Specification<Chapter> specification;
		if (CoProjectUtils.isDigital(id)) {
			specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bookId"), id);
		} else {
			specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bookId"), 1);
		}
		try {
			final List<ChapterDto> chapterDtos = this.chapterRepository.findAll(specification, sort).stream()
					.map(item -> new ChapterDto(item.getId(), item.getName(), item.getNameJp(), item.getBookId()))
					.toList();
			return CoResult.ok(chapterDtos);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<String, PersistenceException> infoStorage(final @NotNull PhraseDto phraseDto) {
		final Long id = Long.parseLong(phraseDto.id());
		final Integer chapterId = phraseDto.chapterId();
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.chapterRepository.findById(chapterId).ifPresentOrElse(val -> {
			final Phrase phrase = new Phrase();
			CoBeanUtils.copyNullableProperties(phraseDto, phrase);
			phrase.setId((chapterId * 1000) + id);
			phrase.setName(val.getName().concat("\u003a").concat(phraseDto.id().toString()));
			phrase.setChapterId(chapterId);
			phrase.setChangeLine(Boolean.FALSE);
			try {
				this.phraseRepository.saveAndFlush(phrase);
				result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_INSERTED));
			} catch (final PersistenceException e) {
				result.setSelf(CoResult.err(e));
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

}
