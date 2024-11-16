package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 権限エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Authority implements Serializable {

	@Serial
	private static final long serialVersionUID = -1152271767975364197L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * タイトル
	 */
	private String title;

	/**
	 * 親ディレクトリID
	 */
	private Long categoryId;
}
