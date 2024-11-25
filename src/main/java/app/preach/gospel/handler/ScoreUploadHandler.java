package app.preach.gospel.handler;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.NONE;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 賛美歌楽譜管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
@Namespace(ProjectURLConstants.URL_HYMNS_NAMESPACE)
@Results({ @Result(name = SUCCESS, location = "/templates/hymns-score-upload.ftl"),
		@Result(name = ERROR, type = "json", params = { "root", "errorResponse" }),
		@Result(name = NONE, type = "json", params = { "root", "responsedJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
public class ScoreUploadHandler extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 4949258675703419344L;

	/**
	 * リクエスト
	 */
	private transient HttpServletRequest servletRequest;

	/**
	 * JSONリスポンス
	 */
	private transient Object responsedJsonData;

	/**
	 * エラーリスポンス
	 */
	private transient String errorResponse;

	/**
	 * 賛美歌サービスインターフェス
	 */
	@Resource
	private IHymnService iHymnService;

	/**
	 * 賛美歌楽譜の情報を保存する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_SCORE_UPLOAD)
	@Override
	public String execute() {
		try {
			// 获取 JSON 数据
			final ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			final Map<String, String> data = mapper.readValue(this.getServletRequest().getReader(), Map.class);
			// 获取参数
			final String editId = data.get("id");
			final String fileData = data.get("score");
			// 将 base64 文件数据解码并保存
			final byte[] fileBytes = Base64.getDecoder().decode(fileData);
			final CoResult<String, PersistenceException> scoreStorage = this.iHymnService.scoreStorage(fileBytes,
					Long.parseLong(editId));
			if (!scoreStorage.isOk()) {
				throw scoreStorage.getErr();
			}
			this.setResponsedJsonData(scoreStorage.getOk());
			return NONE;
		} catch (final IOException e) {
			this.setErrorResponse(e.getMessage());
			ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return ERROR;
		}
	}

	/**
	 * 賛美歌楽譜をダウンロードする
	 *
	 * @return String
	 * @throws IOException
	 */
	@Action(value = ProjectURLConstants.URL_SCORE_DOWNLOAD, results = { @Result(type = "stream") })
	public String scoreDownload() {
		final String scoreId = this.getServletRequest().getParameter("scoreId");
		final CoResult<HymnDto, PersistenceException> hymnInfoById = this.iHymnService
				.getHymnInfoById(Long.parseLong(scoreId));
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getOk();
		ActionContext.getContext().getServletResponse().setContentType(MediaType.APPLICATION_PDF_VALUE);
		ActionContext.getContext().getServletResponse().addHeader(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + hymnDto.id() + ".pdf\"");
		try {
			final ServletOutputStream outputStream = ActionContext.getContext().getServletResponse().getOutputStream();
			outputStream.write(hymnDto.score());
			outputStream.flush();
			outputStream.close();
		} catch (final IOException e) {
			ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			this.setErrorResponse(e.getMessage());
			return ERROR;
		}
		return null;
	}

	/**
	 * 楽譜アプロード画面へ移動する
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_SCORE_UPLOAD)
	public String toScoreUpload() {
		final String scoreId = this.getServletRequest().getParameter("scoreId");
		final String pageNum = this.getServletRequest().getParameter("pageNum");
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_PAGE_NUMBER, pageNum);
		final CoResult<HymnDto, PersistenceException> hymnInfoById = this.iHymnService
				.getHymnInfoById(Long.parseLong(scoreId));
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getOk();
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_EDITED_INFO, hymnDto);
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
