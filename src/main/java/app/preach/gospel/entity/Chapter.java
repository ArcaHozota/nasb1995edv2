package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 章節エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Chapter implements Serializable {

	@Serial
	private static final long serialVersionUID = 3973999295063106134L;

	/**
	 * ID
	 */
	private Integer id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 日本語名称
	 */
	private String nameJp;

	/**
	 * 書別ID
	 */
	private Short bookId;
}
