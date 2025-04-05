package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 節別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "phrases")
@EqualsAndHashCode(callSuper = false)
public final class Phrase implements Serializable {

	@Serial
	private static final long serialVersionUID = 9050047539129107055L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 名称
	 */
	@Column(nullable = false, length = 33)
	private String name;

	/**
	 * 内容
	 */
	@Column(nullable = false)
	private String textEn;

	/**
	 * 日本語内容
	 */
	@Column(nullable = false)
	private String textJp;

	/**
	 * 章節ID
	 */
	@Column(nullable = false)
	private Integer chapterId;

	/**
	 * 改行フラグ
	 */
	@Column(nullable = false)
	private Boolean changeLine;

	/**
	 * 外部キー
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "chapterId", nullable = false, insertable = false, updatable = false)
	private Chapter chapter;
}
