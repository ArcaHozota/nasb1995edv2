package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 書別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
public final class Book implements Serializable {

	@Serial
	private static final long serialVersionUID = 4061917364160208602L;

	/**
	 * ID
	 */
	@Id
	private Short id;

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
	 * 外部キー
	 */
	@OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
	private List<Chapter> chapters;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Book other)) {
			return false;
		}
		return Objects.equals(this.chapters, other.chapters) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.name, other.name) && Objects.equals(this.nameJp, other.nameJp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.chapters, this.id, this.name, this.nameJp);
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "Book [id=" + this.id + ", name=" + this.name + ", nameJp=" + this.nameJp + ", chapters=" + this.chapters
				+ "]";
	}

}
