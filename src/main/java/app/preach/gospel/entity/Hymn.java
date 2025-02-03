package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 賛美歌エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "hymns")
@EqualsAndHashCode(callSuper = false)
public final class Hymn implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String nameJp;

	/**
	 * 韓国語名称
	 */
	@Column
	private String nameKr;

	/**
	 * リンク
	 */
	@Column
	private String link;

	/**
	 * 更新時間
	 */
	@Version
	@Column(nullable = false)
	private OffsetDateTime updatedTime;

	/**
	 * 更新者
	 */
	@Column(nullable = false)
	private Long updatedUser;

	/**
	 * セリフ
	 */
	@Column(columnDefinition = "text")
	private String serif;

	/**
	 * 楽譜
	 */
	@Column(columnDefinition = "bytea")
	private byte[] score;

	/**
	 * 論理削除フラグ
	 */
	@Column(nullable = false)
	private Boolean visibleFlg;
}
