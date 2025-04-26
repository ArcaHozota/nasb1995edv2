package app.preach.gospel.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * 共通返却クラス
 *
 * @param <T> データ
 * @param <E> エラー
 */
@Getter
public final class CoResult<T, E> {

	/**
	 * 異常系を返却する。
	 *
	 * @param <T> 正常
	 * @param <E> エラー
	 * @param err エラー
	 * @return Result<T, E>
	 */
	@Contract(value = "_ -> new", pure = true)
	public static <T, E> @NotNull CoResult<T, E> err(final E err) {
		return new CoResult<>(null, err, false);
	}

	/**
	 * インスタンスを返却する。
	 *
	 * @param <T> 正常
	 * @param <E> エラー
	 * @return Result<T, E>
	 */
	@Contract(value = " -> new", pure = true)
	public static <T, E> @NotNull CoResult<T, E> getInstance() {
		return new CoResult<>(null, null, false);
	}

	/**
	 * 正常系を返却する。
	 *
	 * @param <T>  正常
	 * @param <E>  エラー
	 * @param data データ
	 * @return Result<T, E>
	 */
	@Contract(value = "_ -> new", pure = true)
	public static <T, E> @NotNull CoResult<T, E> ok(final T data) {
		return new CoResult<>(data, null, true);
	}

	/**
	 * 正常的なデータ
	 */
	private T ok;

	/**
	 * エラー
	 */
	private E err;

	/**
	 * 正常系あるかどうか -- GETTER -- 正常系あるかどうかを判断する。
	 * 
	 */
	private boolean isOk;

	/**
	 * コンストラクタ
	 *
	 * @param ok   正常
	 * @param err  エラー
	 * @param isOk 正常系あるかどうか
	 */
	private CoResult(final T ok, final E err, final boolean isOk) {
		this.ok = ok;
		this.err = err;
		this.isOk = isOk;
	}

	/**
	 * setter of err
	 *
	 * @param err エラー
	 */
	private void setErr(final E err) {
		this.err = err;
	}

	/**
	 * setter of isOk
	 *
	 * @param isOk 正常
	 */
	private void setHandan(final boolean isOk) {
		this.isOk = isOk;
	}

	/**
	 * setter of ok
	 *
	 * @param ok 正常
	 */
	private void setOk(final T ok) {
		this.ok = ok;
	}

	/**
	 * 自分を返却する。
	 *
	 * @param self 自分
	 */
	public void setSelf(final @NotNull CoResult<T, E> self) {
		this.setOk(self.getOk());
		this.setErr(self.getErr());
		this.setHandan(self.isOk());
	}

}
