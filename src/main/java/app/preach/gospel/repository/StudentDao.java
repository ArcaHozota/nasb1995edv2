package app.preach.gospel.repository;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import app.preach.gospel.entity.Student;

/**
 * 奉仕者管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Student.class)
public interface StudentDao {

	/**
	 * アカウントの重複性をチェックする
	 *
	 * @param id           ID
	 * @param loginAccount アカウント
	 * @return Integer
	 */
	@SqlQuery("select count(*) from students as st where st.visible_flg = true "
			+ "and st.id <>:id and st.login_account =:loginAccount")
	Integer countDuplicated(Long id, String loginAccount);

	/**
	 * IDによって1件検索する
	 *
	 * @param id ID
	 * @return Student
	 */
	@SqlQuery("select * from students as st where st.visible_flg = true and st.id =:id")
	Student selectById(Long id) throws NoResultsException;

	/**
	 * エンティティによって1件検索する
	 *
	 * @param student エンティティクラス
	 * @return Student
	 */
	@SqlQuery("select * from students as st where st.visible_flg = true and (st.id =:id or st.login_account =:loginAccount or st.password =:password "
			+ "or st.username =:username or st.date_of_birth =:dateOfBirth or st.email =:email or st.updated_time =:updatedTime)")
	Student selectOne(@BindBean Student student);

	/**
	 * 節別の情報を保存する
	 *
	 * @param phrase 節別エンティティ
	 */
	@SqlUpdate("update students set login_account =:loginAccount, password =:password, "
			+ "username =:username, date_of_birth =:dateOfBirth, email =:email, updated_time =:updatedTime "
			+ "where id =:id")
	void updateOne(@BindBean Student student) throws TransactionException;
}
