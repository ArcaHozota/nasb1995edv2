package app.preach.gospel.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * プロジェクトURLコンスタント
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectURLConstants {

	public static final String URL_CATEGORY_NAMESPACE = "/category";

	public static final String URL_BOOKS_NAMESPACE = "/books";

	public static final String URL_HYMNS_NAMESPACE = "/hymns";

	public static final String URL_STUDENTS_NAMESPACE = "/students";

	public static final String URL_CHECK_EDITION = "editionCheck";

	public static final String URL_CHECK_DELETE = "deletionCheck";

	public static final String URL_TO_LOGIN = "login.action";

	public static final String URL_TO_LOGIN_WITH_ERROR = "loginWithError.action";

	public static final String URL_PRE_LOGIN = "preLogin.action";

	public static final String URL_LOG_OUT = "logout";

	public static final String URL_LOGIN = "doLogin";

	public static final String URL_REGISTER = "toroku.action";

	public static final String URL_TO_ERROR = "toSystemError.action";

	public static final String URL_TO_MAINMENU_WITH_LOGIN = "toMainmenuWithLogin.action";

	public static final String URL_TO_ADDITION = "toAddition.action";

	public static final String URL_TO_EDITION = "toEdition.action";

	public static final String URL_STATIC_RESOURCE = "/static/**";
}
