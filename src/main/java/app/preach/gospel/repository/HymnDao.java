package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import app.preach.gospel.entity.Hymn;

/**
 * 賛美歌情報管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Hymn.class)
public interface HymnDao {

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 日本語名称
	 * @return Integer
	 */
	@SqlQuery("select count(*) from hymns as hm where hm.visible_flg = true "
			+ "and hm.id <>:id and hm.name_jp =:nameJp")
	Integer countDuplicated(@Bind("id") Long id, @Bind("nameJp") String nameJp);

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameKr 韓国語名称
	 * @return Integer
	 */
	@SqlQuery("select count(*) from hymns as hm where hm.visible_flg = true "
			+ "and hm.id <>:id and hm.name_kr =:nameKr")
	Integer countDuplicated2(@Bind("id") Long id, @Bind("nameKr") String nameKr);

	/**
	 * ランドム選択検索
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@SqlQuery(value = "select hm.* from hymns as hm inner join hymns_work as hmk on hmk.id = hm.id where hm.visible_flg = true "
			+ "and (hm.name_jp like :keyword or hm.name_kr like :keyword or hm.serif like :keyword or hmk.serif like :keyword2 "
			+ "or cast(hm.id as varchar) like :keyword)")
	List<Hymn> retrieveRandomFive(@Bind("keyword") String keyword, @Bind("keyword2") String keyword2);

	/**
	 * IDによって1件検索する
	 *
	 * @param id ID
	 * @return Hymn
	 */
	@SqlQuery("select * from hymns as hm where hm.visible_flg = true and hm.id =:id")
	Hymn selectById(@Bind("id") Long id) throws NoResultsException;
}
