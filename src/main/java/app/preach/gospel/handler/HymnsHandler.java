package app.preach.gospel.handler;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.NONE;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.io.Serial;
import java.util.List;

import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jdbi.v3.core.JdbiException;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson2.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.Pagination;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 賛美歌管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
@Namespace(ProjectURLConstants.URL_HYMNS_NAMESPACE)
@Results({ @Result(name = SUCCESS, location = "/templates/hymns-pagination.ftl"),
		@Result(name = ERROR, type = "json", params = { "root", "responseError" }),
		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
public class HymnsHandler extends ActionSupport implements ServletRequestAware {

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
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * ID
	 */
	private String id;

	/**
	 * 日本語名称
	 */
	private String nameJp;

	/**
	 * 韓国語名称
	 */
	private String nameKr;

	/**
	 * セリフ
	 */
	private String serif;

	/**
	 * ビデオリンク
	 */
	private String link;

	/**
	 * 更新者
	 */
	private String updatedUser;

	/**
	 * 更新時間
	 */
	private String updatedTime;

	/**
	 * アカウント重複チェック
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_CHECK_NAME)
	public String checkDuplicated() {
		final CoResult<Integer, JdbiException> checkDuplicated = this.iHymnService.checkDuplicated(this.getId(),
				this.getNameJp());
		if (!checkDuplicated.isOk()) {
			throw checkDuplicated.getErr();
		}
		final Integer checkDuplicatedOk = checkDuplicated.getOk();
		if (checkDuplicatedOk >= 1) {
			ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
			this.setResponseError(ProjectConstants.MESSAGE_HYMN_NAME_DUPLICATED);
			return ERROR;
		}
		this.setResponseJsonData(CoProjectUtils.EMPTY_STRING);
		return NONE;
	}

	/**
	 * ランダム五つを検索する
	 *
	 * @return String
	 */
	@Actions(value = { @Action(ProjectURLConstants.URL_COMMON_RETRIEVE),
			@Action(ProjectURLConstants.URL_RANDOM_FIVE_RETRIEVE) })
	public String commonRetrieve() {
		final String keyword = this.getServletRequest().getParameter("keyword");
		final CoResult<List<HymnDto>, JdbiException> hymnsRandomFive = this.iHymnService.getHymnsRandomFive(keyword);
		if (!hymnsRandomFive.isOk()) {
			throw hymnsRandomFive.getErr();
		}
		final List<HymnDto> hymnDtos = hymnsRandomFive.getOk();
		this.setResponseJsonData(JSON.toJSON(hymnDtos));
		return NONE;
	}

	/**
	 * 賛美歌情報転送クラス
	 */
	private HymnDto getHymnDto() {
		return new HymnDto(this.getId(), this.getNameJp(), this.getNameKr(), this.getSerif(), this.getLink(), null,
				this.getUpdatedUser(), this.getUpdatedTime(), null);
	}

	/**
	 * 賛美歌情報を削除する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_INFO_DELETION)
	public String infoDeletion() {
		final String deleteId = this.getServletRequest().getParameter("deleteId");
		final CoResult<String, JdbiException> infoDeletion = this.iHymnService.infoDeletion(Long.parseLong(deleteId));
		if (!infoDeletion.isOk()) {
			throw infoDeletion.getErr();
		}
		this.setResponseJsonData(infoDeletion.getOk());
		return NONE;
	}

	/**
	 * 賛美歌情報を保存する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_INFO_STORAGE, interceptorRefs = { @InterceptorRef("json") })
	public String infoStorage() {
		final CoResult<Integer, JdbiException> infoStorage = this.iHymnService.infoStorage(this.getHymnDto());
		if (!infoStorage.isOk()) {
			throw infoStorage.getErr();
		}
		final Integer pageNum = infoStorage.getOk();
		this.setResponseJsonData(pageNum);
		return NONE;
	}

	/**
	 * 賛美歌情報を更新する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_INFO_UPDATION, interceptorRefs = { @InterceptorRef("json") })
	public String infoUpdation() {
		final CoResult<String, JdbiException> infoUpdation = this.iHymnService.infoUpdation(this.getHymnDto());
		if (!infoUpdation.isOk()) {
			throw infoUpdation.getErr();
		}
		this.setResponseJsonData(infoUpdation.getOk());
		return NONE;
	}

	/**
	 * 情報一覧画面初期表示する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_PAGINATION)
	public String pagination() {
		final String pageNum = this.getServletRequest().getParameter("pageNum");
		final String keyword = this.getServletRequest().getParameter("keyword");
		final CoResult<Pagination<HymnDto>, JdbiException> hymnsByKeyword = this.iHymnService
				.getHymnsByKeyword(Integer.parseInt(pageNum), keyword);
		if (!hymnsByKeyword.isOk()) {
			throw hymnsByKeyword.getErr();
		}
		final Pagination<HymnDto> pagination = hymnsByKeyword.getOk();
		this.setResponseJsonData(JSON.toJSON(pagination));
		return NONE;
	}

	/**
	 * 情報追加画面へ移動する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_TO_ADDITION, results = {
			@Result(name = SUCCESS, location = "/templates/hymns-addition.ftl") })
	public String toAddition() {
		final String pageNum = this.getServletRequest().getParameter("pageNum");
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		return SUCCESS;
	}

	/**
	 * 情報更新画面へ移動する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_TO_EDITION, results = {
			@Result(name = SUCCESS, location = "/templates/hymns-edition.ftl") })
	public String toEdition() {
		final String editId = this.getServletRequest().getParameter("editId");
		final String pageNum = this.getServletRequest().getParameter("pageNum");
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		final CoResult<HymnDto, JdbiException> hymnInfoById = this.iHymnService.getHymnInfoById(Long.parseLong(editId));
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnInfoByIdOk = hymnInfoById.getOk();
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_EDITED_INFO, hymnInfoByIdOk);
		return SUCCESS;
	}

	/**
	 * 情報一覧画面へ移動する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_PAGES)
	public String toPages() {
		final String pageNum = this.getServletRequest().getParameter("pageNum");
		if (CoProjectUtils.isDigital(pageNum)) {
			ActionContext.getContext().put(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
			return SUCCESS;
		}
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_PAGE_NUMBER, "1");
		return SUCCESS;
	}

	/**
	 * ランダム五つ画面へ移動する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_TO_RANDOM_FIVE, results = {
			@Result(name = SUCCESS, location = "/templates/hymns-random-five.ftl") })
	public String toRandomFive() {
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
