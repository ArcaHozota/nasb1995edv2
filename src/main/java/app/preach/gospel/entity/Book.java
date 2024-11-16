package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 書別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Book implements Serializable {

	@Serial
	private static final long serialVersionUID = 4061917364160208602L;

	/**
	 * ID
	 */
	private Short id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 日本語名称
	 */
	private String nameJp;
}
