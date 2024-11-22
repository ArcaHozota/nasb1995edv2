package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.Authority;

/**
 * 権限管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(Authority.class)
public interface AuthorityDao {

	/**
	 * IDリストによって検索する
	 *
	 * @param ids IDリスト
	 * @return List<Authority>
	 */
	@SqlQuery("select * from authorities as at where at.id in (:ids) order by at.id asc")
	List<Authority> findByIds(List<Long> ids) throws NoResultsException;
}
