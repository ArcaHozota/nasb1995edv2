package app.preach.gospel.repository;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

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
}
