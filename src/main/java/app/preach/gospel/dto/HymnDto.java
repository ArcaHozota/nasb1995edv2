package app.preach.gospel.dto;

import java.io.Serializable;

/**
 * 賛美情報転送クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public record HymnDto(

		/**
		 * ID
		 */
		String id,

		/**
		 * 日本語名称
		 */
		String nameJp,

		/**
		 * 韓国語名称
		 */
		String nameKr,

		/**
		 * セリフ
		 */
		String serif,

		/**
		 * ビデオリンク
		 */
		String link,

		/**
		 * 楽譜
		 */
		byte[] score,

		/**
		 * 更新者
		 */
		String updatedUser,

		/**
		 * 更新時間
		 */
		String updatedTime) implements Serializable {
}
