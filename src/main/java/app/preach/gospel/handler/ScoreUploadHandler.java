package app.preach.gospel.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.Base64;
import java.util.Map;

import org.apache.struts2.ActionContext;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.service.IHymnService;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.persistence.PersistenceException;
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
@Controller
@Scope("prototype")
public class ScoreUploadHandler extends DefaultActionSupport implements ServletRequestAware {

	@Serial
	private static final long serialVersionUID = 4949258675703419344L;

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
	 * データ
	 */
	private byte[] fileData;

	/**
	 * ファイル名称
	 */
	private String fileName;

	/**
	 * 内容タイプ
	 */
	private String contentType;

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
			this.setResponseJsonData(scoreStorage.getOk());
			return NONE;
		} catch (final IOException e) {
			this.setResponseError(e.getMessage());
			ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return ERROR;
		}
	}

	/**
	 * ストリームを提供する
	 *
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.getFileData());
	}

	/**
	 * 賛美歌楽譜をダウンロードする
	 *
	 * @return String
	 * @throws IOException
	 */
	public String scoreDownload() {
		final String scoreId = this.getServletRequest().getParameter("scoreId");
		final CoResult<HymnDto, PersistenceException> hymnInfoById = this.iHymnService
				.getHymnInfoById(Long.parseLong(scoreId));
		if (!hymnInfoById.isOk()) {
			throw hymnInfoById.getErr();
		}
		final HymnDto hymnDto = hymnInfoById.getOk();
		if (CoProjectUtils.isEmpty(hymnDto.biko())) {
			this.setContentType("application/pdf");
			this.setFileName(hymnDto.id() + ".pdf");
		} else {
			final String biko = hymnDto.biko();
			final int indexOf = biko.indexOf(CoProjectUtils.SLASH) + 1;
			this.setContentType(biko);
			this.setFileName(hymnDto.id() + ".".concat(biko.substring(indexOf)));
		}
		this.setFileData(hymnDto.score());
		return SUCCESS;
	}

	/**
	 * 楽譜アプロード画面へ移動する
	 *
	 * @return String
	 */
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
