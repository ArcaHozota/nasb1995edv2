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
	 * 更新すべき情報検索
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@Query(value = "select hm from Hymn as hm inner join HymnsWork as hmk on hmk.id = hm.id "
			+ "where hm.visibleFlg = true and (hmk.updatedTime is null or hm.updatedTime > hmk.updatedTime)")
	List<Hymn> findForUpdatedTime();

	/**
	 * ランドム選択検索1
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@Query(value = "select hm from Hymn as hm inner join HymnsWork as hmk on hmk.id = hm.id "
			+ "where hm.visibleFlg = true and (hm.nameJp like :keyword or hm.nameKr like :keyword or hmk.title like :keyword2)")
	List<Hymn> retrieveRandomFive1(@Param("keyword") String keyword, @Param("keyword2") String keyword2);

	/**
	 * ランドム選択検索2
	 *
	 * @param keyword キーワード
	 * @return List<Hymn>
	 */
	@Query(value = "select hm from Hymn as hm inner join HymnsWork as hmk on hmk.id = hm.id "
			+ "where hm.visibleFlg = true and (hm.nameJp like :keyword or hm.nameKr like :keyword or hm.serif like :keyword "
			+ "or hmk.serif like :keyword2 or cast(hm.id as string) like :keyword)")
	List<Hymn> retrieveRandomFive2(@Param("keyword") String keyword, @Param("keyword2") String keyword2);
}
