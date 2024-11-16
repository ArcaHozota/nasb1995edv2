package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 節別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class Phrase implements Serializable {

	@Serial
	private static final long serialVersionUID = 9050047539129107055L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 内容
	 */
	private String textEn;

	/**
	 * 日本語内容
	 */
	private String textJp;

	/**
	 * 章節ID
	 */
	private Integer chapterId;

	/**
	 * 改行フラグ
	 */
	private Boolean changeLine;
}
