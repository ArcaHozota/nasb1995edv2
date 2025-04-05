package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 章節エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chapters")
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Chapter)) {
			return false;
		}
		final Chapter other = (Chapter) obj;
		return Objects.equals(this.book, other.book) && Objects.equals(this.bookId, other.bookId)
				&& Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name)
				&& Objects.equals(this.nameJp, other.nameJp) && Objects.equals(this.phrases, other.phrases);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.book, this.bookId, this.id, this.name, this.nameJp, this.phrases);
	}

	@Override
	public String toString() {
		return "Chapter [id=" + this.id + ", name=" + this.name + ", nameJp=" + this.nameJp + ", bookId=" + this.bookId
				+ ", book=" + this.book + ", phrases=" + this.phrases + "]";
	}

}
