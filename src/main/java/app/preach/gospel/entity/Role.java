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
 * 役割エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
public final class Role implements Serializable {

	@Serial
	private static final long serialVersionUID = 4360593022825424340L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 名称
	 */
	@Column(nullable = false, length = 40)
	private String name;

	/**
	 * 論理削除フラグ
	 */
	@Column(nullable = false)
	private Boolean visibleFlg;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Role)) {
			return false;
		}
		final Role other = (Role) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name)
				&& Objects.equals(this.visibleFlg, other.visibleFlg);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.visibleFlg);
	}

	@Override
	public String toString() {
		return "Role [id=" + this.id + ", name=" + this.name + ", visibleFlg=" + this.visibleFlg + "]";
	}

}
