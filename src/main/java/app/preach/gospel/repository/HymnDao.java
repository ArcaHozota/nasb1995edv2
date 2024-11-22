package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.Hymn;

/**
 * 賛美歌情報管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(Hymn.class)
public interface HymnDao {

	/**
	 * ランドム選択検索
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@SqlQuery(value = "select hm.* from hymns as hm inner join hymns_work as hmk on hmk.id = hm.id where hm.visible_flg = true "
			+ "and (hm.name_jp like :keyword or hm.name_kr like :keyword or hm.serif like :keyword or hmk.serif like :keyword2 "
			+ "or cast(hm.id as varchar) like :keyword)")
	List<Hymn> retrieveRandomFive(String keyword, String keyword2);
}
