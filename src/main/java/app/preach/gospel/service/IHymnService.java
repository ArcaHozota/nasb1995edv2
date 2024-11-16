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
	 * @return CommonResult<Integer, ProjectDataUpdationException>
	 */
	CoResult<Integer, JdbiException> checkDuplicated(String id, String nameJp);

	/**
	 * IDによって歌の情報を取得する
	 *
	 * @param id ID
	 * @return CommonResult<HymnDto, ProjectDataUpdationException>
	 */
	CoResult<HymnDto, JdbiException> getHymnInfoById(Long id);

	/**
	 * キーワードによって賛美歌情報を取得する
	 *
	 * @param pageNum ページ数
	 * @param keyword キーワード
	 * @return CommonResult<Pagination<HymnDto>, ProjectDataUpdationException>
	 */
	CoResult<Pagination<HymnDto>, JdbiException> getHymnsByKeyword(Integer pageNum, String keyword);

	/**
	 * ランドム選択の五つの賛美歌情報を取得する
	 *
	 * @param keyword キーワード
	 * @return CommonResult<List<HymnDto>, ProjectDataUpdationException>
	 */
	CoResult<List<HymnDto>, JdbiException> getHymnsRandomFive(String keyword);

	/**
	 * 賛美歌のレコード数を取得する
	 *
	 * @return CommonResult<Long, ProjectDataUpdationException>
	 */
	CoResult<Long, JdbiException> getTotalRecords();

	/**
	 * 賛美情報を削除する
	 *
	 * @param id ID
	 * @return CommonResult<String, ProjectDataUpdationException>
	 */
	CoResult<String, JdbiException> infoDeletion(Long id);

	/**
	 * 賛美情報を保存する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CommonResult<Integer, ProjectDataUpdationException>
	 */
	CoResult<Integer, JdbiException> infoStorage(HymnDto hymnDto);

	/**
	 * 賛美情報を更新する
	 *
	 * @param hymnDto 賛美情報転送クラス
	 * @return CommonResult<String, ProjectDataUpdationException>
	 */
	CoResult<String, JdbiException> infoUpdation(HymnDto hymnDto);

	/**
	 * 賛美歌楽譜の情報を保存する
	 *
	 * @param file 楽譜ファイル
	 * @param id   ID
	 * @return CommonResult<String, ProjectDataUpdationException>
	 */
	CoResult<String, JdbiException> scoreStorage(byte[] file, Long id);
}
