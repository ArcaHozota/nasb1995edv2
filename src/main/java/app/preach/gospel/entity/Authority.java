package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

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
 * 権限エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "authorities")
@EqualsAndHashCode(callSuper = false)
public final class Authority implements Serializable {

	@Serial
	private static final long serialVersionUID = -1152271767975364197L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * タイトル
	 */
	@Column(nullable = false)
	private String title;

	/**
	 * 親ディレクトリID
	 */
	private Long categoryId;
}
