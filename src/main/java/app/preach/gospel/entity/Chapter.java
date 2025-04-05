package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@Column(nullable = false, length = 33)
	private String name;

	/**
	 * 日本語名称
	 */
	@Column(nullable = false, length = 33)
	private String nameJp;

	/**
	 * 書別ID
	 */
	@Column(nullable = false)
	private Short bookId;

	/**
	 * 外部キー1
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bookId", nullable = false, insertable = false, updatable = false)
	private Book book;

	/**
	 * 外部キー2
	 */
	@OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
	private List<Phrase> phrases;
}
