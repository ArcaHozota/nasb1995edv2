package app.preach.gospel.handler;

import java.io.Serial;
import java.util.List;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson2.JSON;

import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 聖書章節入力ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
//@Namespace(ProjectURLConstants.URL_BOOKS_NAMESPACE)
//@Results({ @Result(name = SUCCESS, location = "/templates/books-addition.ftl"),
//		@Result(name = ERROR, type = "json", params = { "root", "responseError" }),
//		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
//		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
@Scope("prototype")
public class BooksHandler extends DefaultActionSupport implements ServletRequestAware {

	@Serial
	private static final long serialVersionUID = -6535194800678567557L;

	/**
	 * リクエスト
	 */
	private transient HttpServletRequest servletRequest;

	/**
	 * JSONリスポンス
	 */
	private transient Object responseJsonData;

	/**
	 * エラーリスポンス
	 */
	private transient String responseError;

	/**
	 * 聖書章節サービスインターフェス
	 */
	@Resource
	private IBookService iBookService;

	/**
	 * ID
	 */
	private String id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 内容
	 */
	private String textEn;

	/**
	 * 日本語内容
	 */
	private String textJp;

	/**
	 * 章節ID
	 */
	private Integer chapterId;

	/**
	 * 章節情報を取得する
	 *
	 * @return String
	 */
	public String getChapters() {
		final String bookId = this.getServletRequest().getParameter("bookId");
		final CoResult<List<ChapterDto>, PersistenceException> chaptersByBookId = this.iBookService
				.getChaptersByBookId(bookId);
		if (!chaptersByBookId.isOk()) {
			throw chaptersByBookId.getErr();
		}
		final List<ChapterDto> chapterDtos = chaptersByBookId.getOk();
		this.setResponseJsonData(JSON.toJSON(chapterDtos));
		return NONE;
	}

	/**
	 * 節別情報転送クラス
	 */
	private PhraseDto getPhraseDto() {
		return new PhraseDto(this.getId(), this.getName(), this.getTextEn(), this.getTextJp(), this.getChapterId());
	}

	/**
	 * 聖書節別情報を保存する
	 *
	 * @return String
	 */
	public String infoStorage() {
		final CoResult<String, PersistenceException> infoStorage = this.iBookService.infoStorage(this.getPhraseDto());
		if (!infoStorage.isOk()) {
			throw infoStorage.getErr();
		}
		this.setResponseJsonData(infoStorage.getOk());
		return NONE;
	}

	/**
	 * 情報追加画面へ移動する
	 *
	 * @return String
	 */
	public String toAddition() {
		final CoResult<List<BookDto>, PersistenceException> books = this.iBookService.getBooks();
		final CoResult<List<ChapterDto>, PersistenceException> chaptersByBookId = this.iBookService
				.getChaptersByBookId(null);
		if (!books.isOk()) {
			throw books.getErr();
		}
		if (!chaptersByBookId.isOk()) {
			throw chaptersByBookId.getErr();
		}
		ActionContext.getContext().put("bookDtos", books.getOk());
		ActionContext.getContext().put("chapterDtos", chaptersByBookId.getOk());
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
