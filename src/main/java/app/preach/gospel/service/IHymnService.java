package app.preach.gospel.service;

import java.util.List;

import org.jdbi.v3.core.JdbiException;

import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.Pagination;

/**
 * 賛美歌サービスインターフェス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface IHymnService {

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 日本語名称
	 * @return CoResult<Integer, JdbiException>
	 */
	CoResult<Integer, JdbiException> checkDuplicated(String id, String nameJp);

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 韓国語名称
	 * @return CoResult<Integer, DataAccessException>
	 */
	CoResult<Integer, JdbiException> checkDuplicated2(String id, String nameKr);

	/**
	 * IDによって歌の情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<HymnDto, JdbiException>
	 */
	CoResult<HymnDto, JdbiException> getHymnInfoById(Long id);

	/**
	 * キーワードによって賛美歌情報を取得する
	 *
	 * @param pageNum ページ数
	 * @param keyword キーワード
	 * @return CoResult<Pagination<HymnDto>, JdbiException>
	 */
	CoResult<Pagination<HymnDto>, JdbiException> getHymnsByKeyword(Integer pageNum, String keyword);

	/**
	 * ランドム選択の五つの賛美歌情報を取得する
	 *
	 * @param keyword キーワード
	 * @return CoResult<List<HymnDto>, JdbiException>
	 */
	CoResult<List<HymnDto>, JdbiException> getHymnsRandomFive(String keyword);

	/**
	 * 賛美歌のレコード数を取得する
	 *
	 * @return CoResult<Long, JdbiException>
	 */
	CoResult<Long, JdbiException> getTotalRecords();

	/**
	 * 賛美情報を削除する
	 *
	 * @param id ID
	 * @return CoResult<String, JdbiException>
	 */
	CoResult<String, JdbiException> infoDeletion(Long id);

	/**
	 * 賛美情報を保存する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<Integer, JdbiException>
	 */
	CoResult<Integer, JdbiException> infoStorage(HymnDto hymnDto);

	/**
	 * 賛美情報を更新する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<String, JdbiException>
	 */
	CoResult<String, JdbiException> infoUpdation(HymnDto hymnDto);

	/**
	 * 賛美歌楽譜の情報を保存する
	 *
	 * @param file 楽譜ファイル
	 * @param id   ID
	 * @return CoResult<String, JdbiException>
	 */
	CoResult<String, JdbiException> scoreStorage(byte[] file, Long id);
}
