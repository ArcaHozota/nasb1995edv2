package app.preach.gospel.handler;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
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
@Controller
@Scope("prototype")
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
	public String toHomePage() {
		System.out.println(">>>> HomePageHandler#toHomePage() 被调用 <<<<");
		final CoResult<Long, PersistenceException> totalCounts = this.iHymnService.getTotalCounts();
		if (!totalCounts.isOk()) {
			throw totalCounts.getErr();
		}
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_RECORDS, totalCounts.getOk());
		return SUCCESS;
	}

	/**
	 * 一覧表へ移動する
	 *
	 * @return String
	 */
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
