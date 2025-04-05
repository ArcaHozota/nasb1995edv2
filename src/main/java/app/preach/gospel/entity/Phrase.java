package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
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

/**
 * 節別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "phrases")
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Phrase)) {
			return false;
		}
		final Phrase other = (Phrase) obj;
		return Objects.equals(this.changeLine, other.changeLine) && Objects.equals(this.chapter, other.chapter)
				&& Objects.equals(this.chapterId, other.chapterId) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.name, other.name) && Objects.equals(this.textEn, other.textEn)
				&& Objects.equals(this.textJp, other.textJp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.changeLine, this.chapter, this.chapterId, this.id, this.name, this.textEn,
				this.textJp);
	}

	@Override
	public String toString() {
		return "Phrase [id=" + this.id + ", name=" + this.name + ", textEn=" + this.textEn + ", textJp=" + this.textJp
				+ ", chapterId=" + this.chapterId + ", changeLine=" + this.changeLine + ", chapter=" + this.chapter
				+ "]";
	}

}
