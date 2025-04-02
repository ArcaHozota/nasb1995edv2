package app.preach.gospel.utils;

/**
 * LINENUMBER表示クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public enum LineNumber {

	BUNRGUNDY(2),

	CADIMIUM(1),

	NAPLES(3),

	SNOWY(5);

	private final Integer lineNumber;

	// 构造器
	private LineNumber(final Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	// 方法
	public Integer getLineNumber() {
		return this.lineNumber;
	}

}
