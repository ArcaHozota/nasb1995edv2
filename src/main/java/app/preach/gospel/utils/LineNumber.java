package app.preach.gospel.utils;

import lombok.Getter;

/**
 * LINENUMBER表示クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Getter
public enum LineNumber {

	BUNRGUNDY(2),

	CADIMIUM(1),

	NAPLES(3),

	SNOWY(5);

    // 方法
    private final Integer lineNo;

	// 构造器
	LineNumber(final Integer lineNumber) {
		this.lineNo = lineNumber;
	}

}
