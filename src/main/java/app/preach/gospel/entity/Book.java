package app.preach.gospel.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 書別エンティティ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "books")
@EqualsAndHashCode(callSuper = false)
public final class Book implements Serializable {

	private static final long serialVersionUID = 4061917364160208602L;

	/**
	 * ID
	 */
	@Id
	private Short id;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 日本語名称
	 */
	@Column(nullable = false)
	private String nameJp;
}
