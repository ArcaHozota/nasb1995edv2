package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
		if (!(obj instanceof HymnsWork)) {
			return false;
		}
		final HymnsWork other = (HymnsWork) obj;
		return Objects.equals(biko, other.biko) && Objects.equals(hymn, other.hymn) && Objects.equals(id, other.id)
				&& Objects.equals(nameJpRa, other.nameJpRa) && Arrays.equals(score, other.score)
				&& Objects.equals(updatedTime, other.updatedTime) && Objects.equals(workId, other.workId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(score);
		result = prime * result + Objects.hash(biko, hymn, id, nameJpRa, updatedTime, workId);
		return result;
	}

	@Override
	public String toString() {
		return "HymnsWork [id=" + id + ", workId=" + workId + ", score=" + Arrays.toString(score) + ", updatedTime="
				+ updatedTime + ", nameJpRa=" + nameJpRa + ", biko=" + biko + ", hymn=" + hymn + "]";
	}

}
