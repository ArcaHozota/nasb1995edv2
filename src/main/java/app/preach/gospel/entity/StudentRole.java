package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 奉仕者役割連携エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
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
}
