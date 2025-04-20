package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 奉仕者エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "students")
public final class Student implements Serializable {

	@Serial
	private static final long serialVersionUID = 3428604964381066985L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * アカウント
	 */
	@Column(nullable = false, length = 40)
	private String loginAccount;

	/**
	 * ユーザ名称
	 */
	@Column(nullable = false, length = 40)
	private String username;

	/**
	 * パスワード
	 */
	@Column(nullable = false)
	private String password;

	/**
	 * 生年月日
	 */
	@Column(nullable = false)
	private LocalDate dateOfBirth;

	/**
	 * メール
	 */
	@Column(length = 60)
	private String email;

	/**
	 * 役割ID
	 */
	@Column(nullable = false)
	private Long roleId;

	/**
	 * 作成時間
	 */
	@Column
	private OffsetDateTime updatedTime;

	/**
	 * 論理削除フラグ
	 */
	@Column(nullable = false)
	private Boolean visibleFlg;

	/**
	 * 外部キー
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "roleId", nullable = false, insertable = false, updatable = false)
	private Role role;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Student other)) {
			return false;
		}
		return Objects.equals(this.dateOfBirth, other.dateOfBirth) && Objects.equals(this.email, other.email)
				&& Objects.equals(this.id, other.id) && Objects.equals(this.loginAccount, other.loginAccount)
				&& Objects.equals(this.password, other.password) && Objects.equals(this.role, other.role)
				&& Objects.equals(this.roleId, other.roleId) && Objects.equals(this.updatedTime, other.updatedTime)
				&& Objects.equals(this.username, other.username) && Objects.equals(this.visibleFlg, other.visibleFlg);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.dateOfBirth, this.email, this.id, this.loginAccount, this.password, this.role,
				this.roleId, this.updatedTime, this.username, this.visibleFlg);
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "Student [id=" + this.id + ", loginAccount=" + this.loginAccount + ", username=" + this.username
				+ ", password=" + this.password + ", dateOfBirth=" + this.dateOfBirth + ", email=" + this.email
				+ ", roleId=" + this.roleId + ", updatedTime=" + this.updatedTime + ", visibleFlg=" + this.visibleFlg
				+ ", role=" + this.role + "]";
	}

}
