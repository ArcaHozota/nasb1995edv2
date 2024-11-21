package app.preach.gospel.handler;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.NONE;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.io.Serial;

import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jdbi.v3.core.JdbiException;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CoProjectUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * ホームページハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
@Namespace(ProjectURLConstants.URL_HOMEPAGE4)
@Results({ @Result(name = SUCCESS, location = "/templates/index.ftl"),
		@Result(name = ERROR, location = "/templates/system-error.ftl"),
		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
public class HomePageHandler extends ActionSupport implements ServletRequestAware {

	@Serial
	private static final long serialVersionUID = -171237033831060185L;

	/**
	 * リクエスト
	 */
	private transient HttpServletRequest servletRequest;

	/**
	 * JSONリスポンス
	 */
	private transient Object responseJsonData;

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * インデクスへ移動する
	 *
	 * @return String
	 */
	@Actions({ @Action(ProjectURLConstants.URL_HOMEPAGE1), @Action(ProjectURLConstants.URL_HOMEPAGE2),
			@Action(ProjectURLConstants.URL_HOMEPAGE3), @Action(ProjectURLConstants.URL_HOMEPAGE5),
			@Action(CoProjectUtils.EMPTY_STRING) })
	public String toHomePage() {
		final CoResult<Long, JdbiException> totalRecords = this.iHymnService.getTotalRecords();
		if (!totalRecords.isOk()) {
			throw totalRecords.getErr();
		}
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_RECORDS, totalRecords.getOk());
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
