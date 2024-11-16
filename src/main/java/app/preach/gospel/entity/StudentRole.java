package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 奉仕者役割連携エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class StudentRole implements Serializable {

	@Serial
	private static final long serialVersionUID = 9148649412413220711L;

	/**
	 * 奉仕者ID
	 */
	private Long studentId;

	/**
	 * 役割ID
	 */
	private Long roleId;
}
