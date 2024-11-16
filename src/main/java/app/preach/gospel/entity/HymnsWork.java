package app.preach.gospel.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 賛美歌セリフエンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Data
public final class HymnsWork implements Serializable {

	@Serial
	private static final long serialVersionUID = 3956143435944525913L;

	/**
	 * ID
	 */
	private Long id;

	/**
	 * セリフ
	 */
	private String serif;

	/**
	 * 更新時間
	 */
	private OffsetDateTime updatedTime;
}
