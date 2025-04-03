package app.preach.gospel.service;

import java.util.List;

import app.preach.gospel.dto.HymnDto;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.Pagination;
import jakarta.persistence.PersistenceException;

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
	 * @return CoResult<Integer, PersistenceException>
	 */
	CoResult<Integer, PersistenceException> checkDuplicated(String id, String nameJp);

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 韓国語名称
	 * @return CoResult<Integer, DataAccessException>
	 */
	CoResult<Integer, PersistenceException> checkDuplicated2(String id, String nameKr);

	/**
	 * IDによって歌の情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<HymnDto, PersistenceException>
	 */
	CoResult<HymnDto, PersistenceException> getHymnInfoById(Long id);

	/**
	 * キーワードによって賛美歌情報を取得する
	 *
	 * @param pageNum ページ数
	 * @param keyword キーワード
	 * @return CoResult<Pagination<HymnDto>, PersistenceException>
	 */
	CoResult<Pagination<HymnDto>, PersistenceException> getHymnsByKeyword(Integer pageNum, String keyword);

	/**
	 * ランドム選択の五つの賛美歌情報を取得する
	 *
	 * @param keyword キーワード
	 * @return CoResult<List<HymnDto>, PersistenceException>
	 */
	CoResult<List<HymnDto>, PersistenceException> getHymnsRandomFive(String keyword);

	/**
	 * 金海氏の検索によって賛美歌情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<List<HymnDto>, PersistenceException>
	 */
	CoResult<List<HymnDto>, PersistenceException> getKanumiList(Long id);

	/**
	 * 賛美歌のレコードを取得する
	 *
	 * @return CoResult<Long, PersistenceException>
	 */
	CoResult<Long, PersistenceException> getTotalCounts();

	/**
	 * 賛美歌のレコードを取得する
	 *
	 * @return CoResult<List<HymnDto>, PersistenceException>
	 */
	CoResult<List<HymnDto>, PersistenceException> getTotalRecords();

	/**
	 * 賛美情報を削除する
	 *
	 * @param id ID
	 * @return CoResult<String, PersistenceException>
	 */
	CoResult<String, PersistenceException> infoDeletion(Long id);

	/**
	 * 賛美情報を保存する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<Integer, PersistenceException>
	 */
	CoResult<Integer, PersistenceException> infoStorage(HymnDto hymnDto);

	/**
	 * 賛美情報を更新する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<String, PersistenceException>
	 */
	CoResult<String, PersistenceException> infoUpdation(HymnDto hymnDto);

	/**
	 * 賛美歌楽譜の情報を保存する
	 *
	 * @param file 楽譜ファイル
	 * @param id   ID
	 * @return CoResult<String, PersistenceException>
	 */
	CoResult<String, PersistenceException> scoreStorage(byte[] file, Long id);
}
