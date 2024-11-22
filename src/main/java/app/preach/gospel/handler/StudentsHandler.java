package app.preach.gospel.handler;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.LOGIN;
import static com.opensymphony.xwork2.Action.NONE;
import static com.opensymphony.xwork2.Action.SUCCESS;

import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jdbi.v3.core.JdbiException;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 奉仕者管理ハンドラ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
@Setter
@Namespace(ProjectURLConstants.URL_STUDENTS_NAMESPACE)
@Results({ @Result(name = SUCCESS, location = "/templates/students-edition.ftl"),
		@Result(name = ERROR, type = "json", params = { "root", "responseError" }),
		@Result(name = NONE, type = "json", params = { "root", "responseJsonData" }),
		@Result(name = LOGIN, location = "/templates/logintoroku.ftl") })
@Controller
public class StudentsHandler extends ActionSupport implements ServletRequestAware {

	private static final long serialVersionUID = 1592265866534993918L;

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
	 * 奉仕者サービスインターフェス
	 */
	@Resource
	private IStudentService iStudentService;

	/**
	 * ID
	 */
	private String id;

	/**
	 * アカウント
	 */
	private String loginAccount;

	/**
	 * ユーザ名称
	 */
	private String username;

	/**
	 * パスワード
	 */
	private String password;

	/**
	 * メール
	 */
	private String email;

	/**
	 * 生年月日
	 */
	private String dateOfBirth;

	/**
	 * アカウント重複チェック
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_CHECK_NAME)
	public String checkDuplicated() {
		final CoResult<Integer, JdbiException> checkDuplicated = this.iStudentService.checkDuplicated(this.getId(),
				this.getLoginAccount());
		if (!checkDuplicated.isOk()) {
			throw checkDuplicated.getErr();
		}
		final Integer checkDuplicatedOk = checkDuplicated.getOk();
		if (checkDuplicatedOk >= 1) {
			ActionContext.getContext().getServletResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
			this.setResponseError(ProjectConstants.MESSAGE_STUDENT_NAME_DUPLICATED);
			return ERROR;
		}
		this.setResponseJsonData(CoProjectUtils.EMPTY_STRING);
		return NONE;
	}

	/**
	 * 奉仕者情報転送クラス
	 */
	private StudentDto getStudentDto() {
		return new StudentDto(this.getId(), this.getLoginAccount(), this.getUsername(), this.getPassword(),
				this.getEmail(), this.getDateOfBirth(), null);
	}

	/**
	 * 奉仕者情報を更新する
	 *
	 * @return String
	 */
	@Action(value = ProjectURLConstants.URL_INFO_UPDATION, interceptorRefs = { @InterceptorRef("json") })
	public String infoUpdation() {
		final CoResult<String, JdbiException> infoUpdation = this.iStudentService.infoUpdation(this.getStudentDto());
		if (!infoUpdation.isOk()) {
			throw infoUpdation.getErr();
		}
		this.setResponseJsonData(infoUpdation.getOk());
		return NONE;
	}

	/**
	 * ログイン時間記録
	 *
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_PRE_LOGIN)
	public String preLogin() {
		final CoResult<String, JdbiException> preLoginUpdation = this.iStudentService.preLoginUpdation(
				this.getServletRequest().getParameter("loginAccount"),
				this.getServletRequest().getParameter("password"));
		if (!preLoginUpdation.isOk()) {
			throw preLoginUpdation.getErr();
		}
		this.setResponseJsonData(preLoginUpdation.getOk());
		return NONE;
	}

	/**
	 * 情報更新画面へ移動する
	 *
	 * @param editId 編集ID
	 * @return String
	 */
	@Action(ProjectURLConstants.URL_TO_EDITION)
	public String toEdition() {
		final String editId = this.getServletRequest().getParameter("editId");
		final CoResult<StudentDto, JdbiException> studentInfoById = this.iStudentService
				.getStudentInfoById(Long.parseLong(editId));
		if (!studentInfoById.isOk()) {
			throw studentInfoById.getErr();
		}
		final StudentDto studentDto = studentInfoById.getOk();
		ActionContext.getContext().put(ProjectConstants.ATTRNAME_EDITED_INFO, studentDto);
		return SUCCESS;
	}

	@Override
	public void withServletRequest(final HttpServletRequest request) {
		this.servletRequest = request;
	}

}
