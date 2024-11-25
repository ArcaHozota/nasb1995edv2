package app.preach.gospel.service;

import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.utils.CoResult;
import jakarta.persistence.PersistenceException;

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
	 * @return CoResult<Integer, PersistenceException>
	 */
	CoResult<Integer, PersistenceException> checkDuplicated(String id, String loginAccount);

	/**
	 * IDによって奉仕者の情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<StudentDto, PersistenceException>
	 */
	CoResult<StudentDto, PersistenceException> getStudentInfoById(Long id);

	/**
	 * 奉仕者情報を更新する
	 *
	 * @param studentDto 奉仕者情報転送クラス
	 * @return CoResult<String, PersistenceException>
	 */
	CoResult<String, PersistenceException> infoUpdation(StudentDto studentDto);

	/**
	 * ログイン時間記録
	 *
	 * @param loginAccount アカウント
	 * @param password     パスワード
	 * @return CoResult<String, PersistenceException>
	 */
	CoResult<String, PersistenceException> preLoginUpdation(String loginAccount, String password);
}
