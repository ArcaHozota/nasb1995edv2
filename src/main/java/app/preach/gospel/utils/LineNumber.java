package app.preach.gospel.utils;

/**
 * LINENUMBER表示クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public enum LineNumber {

	BUNRGUNDY("Burgundy Red"),

	CADIMIUM("Cadmium Green"),

	NAPLES("Napoli Yellow"),

	SNOWY("Standard White");

	private final String color;

	// 构造器
	private LineNumber(final String color) {
		this.color = color;
	}

	// 方法
	public String getColor() {
		return this.color;
	}

}
