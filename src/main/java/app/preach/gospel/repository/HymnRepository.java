package app.preach.gospel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.preach.gospel.entity.Hymn;

/**
 * 賛美歌情報管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface HymnRepository extends JpaRepository<Hymn, Long>, JpaSpecificationExecutor<Hymn> {

	/**
	 * 怪しいキーワードに対して情報検索
	 *
	 * @return List<Hymn>
	 */
	@Query(value = "select hm from Hymn as hm where hm.visibleFlg = true order by hm.id asc limit 5")
	List<Hymn> findForStrangement();

	/**
	 * ランドム選択検索
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@Query(value = "select hm from Hymn as hm inner join HymnsWork as hmk on hmk.workId= hm.id "
			+ "where hm.visibleFlg = true and (hm.nameJp like :keyword or hm.nameKr like :keyword "
			+ "or hm.serif like :keyword or hmk.nameJpRa like :keyword or cast(hm.id as string) like :keyword)")
	List<Hymn> retrieveRandomFive(@Param("keyword") String keyword);
}
