package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 役割権限情報ID
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class RoleAuthIds implements Serializable {

	@Serial
	private static final long serialVersionUID = -297785511370318383L;

	/**
	 * 権限ID
	 */
	private Long roleId;

	/**
	 * 役割ID
	 */
	private Long authId;
}
