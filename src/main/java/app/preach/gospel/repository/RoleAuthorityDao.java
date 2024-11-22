package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.RoleAuthority;

/**
 * 役割権限連携リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(RoleAuthority.class)
public interface RoleAuthorityDao {

	/**
	 * IDリストによって検索する
	 *
	 * @param roleId 役割ID
	 * @return List<RoleAuthority>
	 */
	@SqlQuery("select * from role_auth as ra where ra.role_id =:roleId")
	List<RoleAuthority> findByRoleId(Long roleId);
}
