package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 賛美歌セリフエンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hymns_work")
public final class HymnsWork implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * ワークID
	 */
	@Column(nullable = false)
	private Long workId;

	/**
	 * 楽譜
	 */
	@Column(columnDefinition = "bytea")
	private byte[] score;

	/**
	 * 更新時間
	 */
	@Version
	@Column(nullable = false)
	private OffsetDateTime updatedTime;

	/**
	 * 日本語名称
	 */
	@Column(name = "nameJpRational", length = 120)
	private String nameJpRa;

	/**
	 * 備考
	 */
	@Column(length = 10)
	private String biko;

	/**
	 * 外部キー
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "workId", nullable = false, insertable = false, updatable = false)
	private Hymn hymn;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof final HymnsWork other)) {
			return false;
		}
		return Objects.equals(this.biko, other.biko) && Objects.equals(this.hymn, other.hymn)
				&& Objects.equals(this.id, other.id) && Objects.equals(this.nameJpRa, other.nameJpRa)
				&& Arrays.equals(this.score, other.score) && Objects.equals(this.updatedTime, other.updatedTime)
				&& Objects.equals(this.workId, other.workId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.score);
		result = prime * result
				+ Objects.hash(this.biko, this.hymn, this.id, this.nameJpRa, this.updatedTime, this.workId);
		return result;
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "HymnsWork [id=" + this.id + ", workId=" + this.workId + ", score=" + Arrays.toString(this.score)
				+ ", updatedTime=" + this.updatedTime + ", nameJpRa=" + this.nameJpRa + ", biko=" + this.biko
				+ ", hymn=" + this.hymn + "]";
	}

}
