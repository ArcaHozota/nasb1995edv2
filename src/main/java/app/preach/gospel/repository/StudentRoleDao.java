package app.preach.gospel.repository;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import app.preach.gospel.entity.StudentRole;

/**
 * 奉仕者役割連携リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(StudentRole.class)
public interface StudentRoleDao {

	/**
	 * IDによって1件検索する
	 *
	 * @param id ID
	 * @return StudentRole
	 */
	@SqlQuery("select * from student_role as str where str.student_id =:id")
	StudentRole selectById(Long id) throws NoResultsException;
}
