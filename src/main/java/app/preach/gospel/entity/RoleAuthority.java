package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 役割権限連携エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "role_auth")
@IdClass(RoleAuthIds.class)
public final class RoleAuthority implements Serializable {

	@Serial
	private static final long serialVersionUID = 4995165208601855074L;

	/**
	 * 権限ID
	 */
	@Id
	private Long authId;

	/**
	 * 役割ID
	 */
	@Id
	private Long roleId;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final RoleAuthority other)) {
			return false;
		}
		return Objects.equals(this.authId, other.authId) && Objects.equals(this.roleId, other.roleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.authId, this.roleId);
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "RoleAuthority [authId=" + this.authId + ", roleId=" + this.roleId + "]";
	}

}
