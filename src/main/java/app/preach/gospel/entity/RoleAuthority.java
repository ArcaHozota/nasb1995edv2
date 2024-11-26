package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 役割権限連携エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
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
}
