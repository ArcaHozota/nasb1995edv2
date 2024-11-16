package app.preach.gospel.service;

import org.jdbi.v3.core.JdbiException;

import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.utils.CoResult;

/**
 * 奉仕者サービスインターフェス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface IStudentService {

	/**
	 * アカウントの重複性をチェックする
	 *
	 * @param id           ID
	 * @param loginAccount アカウント
	 * @return CommonResult<Integer, ProjectDataUpdationException>
	 */
	CoResult<Integer, JdbiException> checkDuplicated(String id, String loginAccount);

	/**
	 * IDによって奉仕者の情報を取得する
	 *
	 * @param id ID
	 * @return CommonResult<StudentDto, ProjectDataUpdationException>
	 */
	CoResult<StudentDto, JdbiException> getStudentInfoById(Long id);

	/**
	 * 奉仕者情報を更新する
	 *
	 * @param studentDto 奉仕者情報転送クラス
	 * @return CommonResult<String, ProjectDataUpdationException>
	 */
	CoResult<String, JdbiException> infoUpdation(StudentDto studentDto);

	/**
	 * ログイン時間記録
	 *
	 * @param loginAccount アカウント
	 * @param password     パスワード
	 * @return CommonResult<String, ProjectDataUpdationException>
	 */
	CoResult<String, JdbiException> preLoginUpdation(String loginAccount, String password);
}
