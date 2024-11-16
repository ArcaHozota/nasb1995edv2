package app.preach.gospel.handler;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.NONE;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.utils.CommonProjectUtils;
import jakarta.servlet.ServletOutputStream;
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
@Namespace(ProjectURLConstants.URL_CATEGORY_NAMESPACE)
@Results({ @Result(name = SUCCESS, location = "/templates/mainmenu.ftl"),
		@Result(name = ERROR, location = "/templates/system-error.ftl"),
		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
public class CategoryHandler extends ActionSupport implements ServletRequestAware {

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
	private final ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * クロスアイコン取得する
	 *
	 * @return String
     */
	@Action(value = ProjectURLConstants.URL_INITIAL_TEMPLATE, results = { @Result(type = "stream") })
	public String initial() throws IOException {
		final String svgSource = this.getServletRequest().getParameter("icons");
		final Resource resource = this.getResourceLoader().getResource("classpath:/static/image/" + svgSource);
		final InputStream inputStream = resource.getInputStream();
		final byte[] buffer = new byte[(int) resource.getFile().length()];
		inputStream.read(buffer);
		inputStream.close();
		ActionContext.getContext().getServletResponse().setContentType("image/svg+xml");
		ActionContext.getContext().getServletResponse().setCharacterEncoding(CommonProjectUtils.CHARSET_UTF8.name());
		final ServletOutputStream outputStream = ActionContext.getContext().getServletResponse().getOutputStream();
		outputStream.write(buffer);
		outputStream.flush();
		outputStream.close();
		return null;
	}

	/**
	 * ログイン画面初期表示する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_LOGIN)
	public String login() {
		return LOGIN;
	}

	/**
	 * ログイン画面へ移動する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_LOGIN_WITH_ERROR)
	public String loginWithError() {
		ActionContext.getContext().put("torokuMsg", ProjectConstants.MESSAGE_STRING_NOTLOGIN);
		return LOGIN;
	}

	/**
	 * メインメニューアイコン取得する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_MENU_INITIAL, results = { @Result(type = "stream") })
	public String menuInitial() throws IOException {
		final String svgSource = this.getServletRequest().getParameter("icons");
		final Resource resource = this.getResourceLoader().getResource("classpath:/static/image/icons/" + svgSource);
		final InputStream inputStream = resource.getInputStream();
		final byte[] buffer = new byte[(int) resource.getFile().length()];
		inputStream.read(buffer);
		inputStream.close();
		ActionContext.getContext().getServletResponse().setContentType("image/svg+xml");
		ActionContext.getContext().getServletResponse().setCharacterEncoding(CommonProjectUtils.CHARSET_UTF8.name());
		final ServletOutputStream outputStream = ActionContext.getContext().getServletResponse().getOutputStream();
		outputStream.write(buffer);
		outputStream.flush();
		outputStream.close();
		return null;
	}

	/**
	 * メインメニューへ移動する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_MAINMENU)
	public String toMainmenu() {
		return SUCCESS;
	}

	/**
	 * システムエラー画面へ移動する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_ERROR)
	public String toSystemError() {
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_EXCEPTION,
				ProjectConstants.MESSAGE_STRING_FATAL_ERROR);
		ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return ERROR;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
