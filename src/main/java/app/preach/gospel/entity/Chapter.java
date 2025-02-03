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
 * 章節エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "chapters")
@EqualsAndHashCode(callSuper = false)
public final class Chapter implements Serializable {

	@Serial
	private static final long serialVersionUID = 3973999295063106134L;

	/**
	 * ID
	 */
	@Id
	private Integer id;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 日本語名称
	 */
	@Column(nullable = false)
	private String nameJp;

	/**
	 * 書別ID
	 */
	@Column(nullable = false)
	private Short bookId;
}
