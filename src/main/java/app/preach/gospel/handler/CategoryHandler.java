package app.preach.gospel.handler;

import java.io.Serial;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 共通とSVGイメージハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
@Controller
@Scope("prototype")
public class CategoryHandler extends DefaultActionSupport implements ServletRequestAware {

	@Serial
	private static final long serialVersionUID = -3971408230922185628L;

	/**
	 * リクエスト
	 */
	private transient HttpServletRequest servletRequest;

	/**
	 * JSONリスポンス
	 */
	private transient Object responseJsonData;

	/**
	 * ResourceLoader
	 */
	private transient ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * ログイン画面初期表示する
	 *
	 * @return String
	 */
	public String login() {
		return LOGIN;
	}

	/**
	 * ログイン画面へ移動する
	 *
	 * @return String
	 * @throws Exception 例外
	 */
	public String loginWithError() throws Exception {
		final CoResult<Long, Exception> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getOk());
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_TOROKU_MSG, ProjectConstants.MESSAGE_STRING_NOT_LOGIN);
		return LOGIN;
	}

	/**
	 * メインメニューへ移動する
	 *
	 * @return String
	 */
	public String toMainmenu() {
		return SUCCESS;
	}

	/**
	 * メインメニューへ移動する
	 *
	 * @return String
	 */
	public String toMainmenuWithLogin() {
		ActionContext.getContext().put("loginMsg", ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS);
		return SUCCESS;
	}

	/**
	 * システムエラー画面へ移動する
	 *
	 * @return String
	 */
	public String toSystemError() {
		final String errorMsg = this.getServletRequest().getParameter(ProjectConstants.ATTRNAME_EXCEPTION);
		if (CoProjectUtils.isEmpty(errorMsg)) {
			ActionContext.getContext().put(ProjectConstants.ATTRNAME_EXCEPTION,
					ProjectConstants.MESSAGE_STRING_UNEXPECTED_ERROR);
		} else if (errorMsg.length() > 120) {
			ActionContext.getContext().put(ProjectConstants.ATTRNAME_EXCEPTION, errorMsg.substring(0, 120));
		} else {
			ActionContext.getContext().put(ProjectConstants.ATTRNAME_EXCEPTION, errorMsg);
		}
		ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
