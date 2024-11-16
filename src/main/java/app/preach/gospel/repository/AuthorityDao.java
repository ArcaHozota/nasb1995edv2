package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import app.preach.gospel.entity.Authority;

/**
 * 権限管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Authority.class)
public interface AuthorityDao {

	/**
	 * IDリストによって検索する
	 *
	 * @param ids IDリスト
	 * @return List<Authority>
	 */
	@SqlQuery("select * from authorities as at where at.id in (:ids)")
	List<Authority> findByIds(List<Long> ids);
}
