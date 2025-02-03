package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 賛美歌セリフエンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "hymns_work")
@EqualsAndHashCode(callSuper = false)
public final class HymnsWork implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 歌の名称
	 */
	@Column
	private String title;

	/**
	 * セリフ
	 */
	@Column(columnDefinition = "text")
	private String serif;

	/**
	 * 更新時間
	 */
	@Column
	private OffsetDateTime updatedTime;
}
