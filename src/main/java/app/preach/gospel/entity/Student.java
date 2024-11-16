package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 奉仕者エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Student implements Serializable {

	@Serial
	private static final long serialVersionUID = 3428604964381066985L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * アカウント
	 */
	private String loginAccount;

	/**
	 * ユーザ名称
	 */
	private String username;

	/**
	 * パスワード
	 */
	private String password;

	/**
	 * 生年月日
	 */
	private LocalDate dateOfBirth;

	/**
	 * メール
	 */
	private String email;

	/**
	 * 作成時間
	 */
	private OffsetDateTime updatedTime;

	/**
	 * 論理削除フラグ
	 */
	private Boolean visibleFlg;
}
