package app.preach.gospel.service;

import java.util.List;

import org.jdbi.v3.core.JdbiException;

import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.utils.CoResult;

/**
 * 聖書章節サービスインターフェス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface IBookService {

	/**
	 * 聖書書別情報を取得する
	 *
	 * @return CoResult<List<BookDto>, JdbiException>
	 */
	CoResult<List<BookDto>, JdbiException> getBooks();

	/**
	 * 聖書章節情報を取得する
	 *
	 * @param id 書別ID
	 * @return CoResult<List<ChapterDto>, JdbiException>
	 */
	CoResult<List<ChapterDto>, JdbiException> getChaptersByBookId(String id);

	/**
	 * 聖書節別情報を保存する
	 *
	 * @param phraseDto 節別情報転送クラス
	 * @return CoResult<String, JdbiException>
	 */
	CoResult<String, JdbiException> infoStorage(PhraseDto phraseDto);
}
