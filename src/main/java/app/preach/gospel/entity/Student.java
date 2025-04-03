package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 奉仕者エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "students")
@EqualsAndHashCode(callSuper = false)
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
	@Column(nullable = false, length = 120)
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
	 * 作成時間
	 */
	@Column
	private OffsetDateTime updatedTime;

	/**
	 * 論理削除フラグ
	 */
	@Column(nullable = false)
	private Boolean visibleFlg;
}
