package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 権限エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "authorities")
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
	@Column(nullable = false, length = 50)
	private String name;

	/**
	 * タイトル
	 */
	@Column(nullable = false, length = 40)
	private String title;

	/**
	 * 親ディレクトリID
	 */
	private Long categoryId;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Authority)) {
			return false;
		}
		final Authority other = (Authority) obj;
		return Objects.equals(this.categoryId, other.categoryId) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.name, other.name) && Objects.equals(this.title, other.title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.categoryId, this.id, this.name, this.title);
	}

	@Override
	public String toString() {
		return "Authority [id=" + this.id + ", name=" + this.name + ", title=" + this.title + ", categoryId="
				+ this.categoryId + "]";
	}

}
