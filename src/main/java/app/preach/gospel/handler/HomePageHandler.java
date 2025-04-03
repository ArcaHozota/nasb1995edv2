package app.preach.gospel.handler;

import static org.apache.struts2.action.Action.ERROR;
import static org.apache.struts2.action.Action.LOGIN;
import static org.apache.struts2.action.Action.NONE;
import static org.apache.struts2.action.Action.SUCCESS;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.persistence.PersistenceException;
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
@Namespace("/")
@Results({ @Result(name = SUCCESS, location = "/templates/index.ftl"),
		@Result(name = ERROR, location = "/templates/system-error.ftl"),
		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Scope("prototype")
@Controller
public class HomePageHandler extends DefaultActionSupport implements ServletRequestAware {

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
	@Actions({ @Action("home"), @Action("homePage"),
			@Action(ProjectURLConstants.URL_HOMEPAGE3), @Action(ProjectURLConstants.URL_HOMEPAGE5),
			@Action(CoProjectUtils.EMPTY_STRING) })
	public String toHomePage() {
		final CoResult<List<HymnDto>, PersistenceException> totalRecords = this.iHymnService.getTotalRecords();
		if (!totalRecords.isOk()) {
			throw totalRecords.getErr();
		}
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_RECORDS, totalRecords.getOk().size());
		return SUCCESS;
	}

	/**
	 * 一覧表へ移動する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_LEDGER, results = {
			@Result(name = SUCCESS, location = "/templates/index2.ftl") })
	public String toIchiranhyo() {
		final CoResult<List<HymnDto>, PersistenceException> totalRecords = this.iHymnService.getTotalRecords();
		if (!totalRecords.isOk()) {
			throw totalRecords.getErr();
		}
		final List<HymnDto> hymnDtos = new ArrayList<>();
		hymnDtos.add(new HymnDto(String.valueOf(0), ProjectConstants.DEFAULT_LEDGER_NAME, null, null, null, null, null,
				null, null));
		hymnDtos.addAll(totalRecords.getOk());
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_RECORDS, totalRecords.getOk().size());
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_HYMNDTOS, hymnDtos);
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
