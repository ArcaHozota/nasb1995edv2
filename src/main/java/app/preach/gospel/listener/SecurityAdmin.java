package app.preach.gospel.listener;

import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import app.preach.gospel.dto.StudentDto;
import lombok.EqualsAndHashCode;

/**
 * User拡張クラス(SpringSecurity関連)
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@EqualsAndHashCode(callSuper = false)
public final class SecurityAdmin extends User {

	private static final long serialVersionUID = 3827955098466369880L;

	/**
	 * 社員管理DTO
	 */
	private final StudentDto originalAdmin;

	/**
	 * コンストラクタ
	 *
	 * @param admin       社員管理DTO
	 * @param authorities 権限リスト
	 */
	SecurityAdmin(final StudentDto admin, final Collection<SimpleGrantedAuthority> authorities) {
		super(admin.loginAccount(), admin.password(), true, true, true, true, authorities);
		this.originalAdmin = admin;
	}

	/**
	 * getter for originalAdmin
	 *
	 * @return EmployeeDto
	 */
	public StudentDto getOriginalAdmin() {
		return this.originalAdmin;
	}
}
