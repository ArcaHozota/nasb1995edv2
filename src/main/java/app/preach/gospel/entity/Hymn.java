package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 賛美歌エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Hymn implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String nameJp;

	/**
	 * 韓国語名称
	 */
	private String nameKr;

	/**
	 * リンク
	 */
	private String link;

	/**
	 * 更新時間
	 */
	private OffsetDateTime updatedTime;

	/**
	 * 更新者
	 */
	private Long updatedUser;

	/**
	 * セリフ
	 */
	private String serif;

	/**
	 * 楽譜
	 */
	private byte[] score;

	/**
	 * 論理削除フラグ
	 */
	private Boolean visibleFlg;
}
