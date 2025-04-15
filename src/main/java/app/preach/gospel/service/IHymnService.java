package app.preach.gospel.service;

import java.util.List;

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
	 * @return CoResult<Integer, Exception>
	 */
	CoResult<Integer, Exception> checkDuplicated(String id, String nameJp);

	/**
	 * 歌の名称の重複性をチェックする
	 *
	 * @param id     ID
	 * @param nameJp 韓国語名称
	 * @return CoResult<Integer, DataAccessException>
	 */
	CoResult<Integer, Exception> checkDuplicated2(String id, String nameKr);

	/**
	 * IDによって歌の情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<HymnDto, Exception>
	 */
	CoResult<HymnDto, Exception> getHymnInfoById(Long id);

	/**
	 * キーワードによって賛美歌情報を取得する
	 *
	 * @param pageNum ページ数
	 * @param keyword キーワード
	 * @return CoResult<Pagination<HymnDto>, Exception>
	 */
	CoResult<Pagination<HymnDto>, Exception> getHymnsByKeyword(Integer pageNum, String keyword);

	/**
	 * ランドム選択の五つの賛美歌情報を取得する
	 *
	 * @param keyword キーワード
	 * @return CoResult<List<HymnDto>, Exception>
	 */
	CoResult<List<HymnDto>, Exception> getHymnsRandomFive(String keyword);

	/**
	 * 金海氏の検索によって賛美歌情報を取得する
	 *
	 * @param id ID
	 * @return CoResult<List<HymnDto>, Exception>
	 */
	CoResult<List<HymnDto>, Exception> getKanumiList(Long id);

	/**
	 * 賛美歌のレコード数を取得する
	 *
	 * @return CoResult<Long, Exception>
	 */
	CoResult<Long, Exception> getTotalCounts();

	/**
	 * 賛美情報を削除する
	 *
	 * @param id ID
	 * @return CoResult<String, Exception>
	 */
	CoResult<String, Exception> infoDeletion(Long id);

	/**
	 * 賛美情報を保存する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<Integer, Exception>
	 */
	CoResult<Integer, Exception> infoStorage(HymnDto hymnDto);

	/**
	 * 賛美情報を更新する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CoResult<String, Exception>
	 */
	CoResult<String, Exception> infoUpdation(HymnDto hymnDto);

	/**
	 * 賛美歌楽譜の情報を保存する
	 *
	 * @param file 楽譜ファイル
	 * @param id   ID
	 * @return CoResult<String, Exception>
	 */
	CoResult<String, Exception> scoreStorage(byte[] file, Long id);
}
