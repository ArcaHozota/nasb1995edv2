package app.preach.gospel.handler;

import java.io.Serial;
import java.util.List;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson2.JSON;

import app.preach.gospel.dto.BookDto;
import app.preach.gospel.dto.ChapterDto;
import app.preach.gospel.dto.PhraseDto;
import app.preach.gospel.service.IBookService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
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
	private String chapterId;

	/**
	 * 章節情報を取得する
	 *
	 * @return String
	 * @throws Exception 例外
	 */
	public String getChapters() throws Exception {
		final String bookId = this.getServletRequest().getParameter("bookId");
		final CoResult<List<ChapterDto>, Exception> chaptersByBookId = this.iBookService.getChaptersByBookId(bookId);
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
	@Contract(" -> new")
	private @NotNull PhraseDto getPhraseDto() {
		return new PhraseDto(this.getId(), this.getName(), this.getTextEn(), this.getTextJp(), this.getChapterId());
	}

	/**
	 * 聖書節別情報を保存する
	 *
	 * @return String
	 * @throws Exception 例外
	 */
	public String infoStorage() throws Exception {
		final CoResult<String, Exception> infoStorage = this.iBookService.infoStorage(this.getPhraseDto());
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
	 * @throws Exception 例外
	 */
	public String toAddition() throws Exception {
		final CoResult<List<BookDto>, Exception> books = this.iBookService.getBooks();
		final CoResult<List<ChapterDto>, Exception> chaptersByBookId = this.iBookService.getChaptersByBookId(null);
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
