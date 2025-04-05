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

	private final Integer lineNo;

	// 构造器
	private LineNumber(final Integer lineNumber) {
		this.lineNo = lineNumber;
	}

	// 方法
	public Integer getLineNo() {
		return this.lineNo;
	}

}
