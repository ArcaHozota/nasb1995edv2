package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 賛美歌エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "hymns")
public final class Hymn implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	@Id
	private Long id;

	/**
	 * 名称
	 */
	@Column(nullable = false, length = 66)
	private String nameJp;

	/**
	 * 韓国語名称
	 */
	@Column(length = 66)
	private String nameKr;

	/**
	 * リンク
	 */
	@Column
	private String link;

	/**
	 * 更新時間
	 */
	@Version
	@Column(nullable = false)
	private OffsetDateTime updatedTime;

	/**
	 * 更新者
	 */
	@Column(nullable = false)
	private Long updatedUser;

	/**
	 * セリフ
	 */
	@Column(columnDefinition = "text")
	private String serif;

	/**
	 * 論理削除フラグ
	 */
	@Column(nullable = false)
	private Boolean visibleFlg;

	/**
	 * 外部キー
	 */
	@OneToOne(mappedBy = "hymn", fetch = FetchType.LAZY)
	private HymnsWork hymnsWork;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Hymn other)) {
			return false;
		}
		return Objects.equals(this.hymnsWork, other.hymnsWork) && Objects.equals(this.id, other.id)
				&& Objects.equals(this.link, other.link) && Objects.equals(this.nameJp, other.nameJp)
				&& Objects.equals(this.nameKr, other.nameKr) && Objects.equals(this.serif, other.serif)
				&& Objects.equals(this.updatedTime, other.updatedTime)
				&& Objects.equals(this.updatedUser, other.updatedUser)
				&& Objects.equals(this.visibleFlg, other.visibleFlg);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.hymnsWork, this.id, this.link, this.nameJp, this.nameKr, this.serif, this.updatedTime,
				this.updatedUser, this.visibleFlg);
	}

	@Contract(pure = true)
	@Override
	public @NotNull String toString() {
		return "Hymn [id=" + this.id + ", nameJp=" + this.nameJp + ", nameKr=" + this.nameKr + ", link=" + this.link
				+ ", updatedTime=" + this.updatedTime + ", updatedUser=" + this.updatedUser + ", serif=" + this.serif
				+ ", visibleFlg=" + this.visibleFlg + ", hymnsWork=" + this.hymnsWork + "]";
	}

}
