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
 * 奉仕者役割連携エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "student_role")
public final class StudentRole implements Serializable {

	@Serial
	private static final long serialVersionUID = 9148649412413220711L;

	/**
	 * 奉仕者ID
	 */
	@Id
	private Long studentId;

	/**
	 * 役割ID
	 */
	@Column(nullable = false)
	private Long roleId;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StudentRole)) {
			return false;
		}
		final StudentRole other = (StudentRole) obj;
		return Objects.equals(this.roleId, other.roleId) && Objects.equals(this.studentId, other.studentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.roleId, this.studentId);
	}

	@Override
	public String toString() {
		return "StudentRole [studentId=" + this.studentId + ", roleId=" + this.roleId + "]";
	}

}
