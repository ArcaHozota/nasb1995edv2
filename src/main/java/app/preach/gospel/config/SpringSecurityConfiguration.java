package app.preach.gospel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.common.ProjectURLConstants;
import app.preach.gospel.listener.ProjectUserDetailsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringSecurity配置クラス
 *
 * @author ArkamaHozota
 * @version
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

	private static final String SLASH = "\u002f";

	/**
	 * ログインサービス
	 */
	@Resource
	private ProjectUserDetailsService projectUserDetailsService;

	/**
	 * ログインエラー処理
	 */
	@Resource
	private ProjectAuthenticationEntryPoint projectAuthenticationEntryPoint;

	@Bean
	@Order(1)
	protected AuthenticationManager authenticationManager(final AuthenticationManagerBuilder authBuilder) {
		return authBuilder.authenticationProvider(this.daoAuthenticationProvider()).getObject();
	}

	@Bean
	@Order(0)
	protected DaoAuthenticationProvider daoAuthenticationProvider() {
		final ProjectDaoAuthenticationProvider provider = new ProjectDaoAuthenticationProvider();
		provider.setUserDetailsService(this.projectUserDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder(BCryptVersion.$2A, 7));
		return provider;
	}

	@Bean
	@Order(2)
	protected SecurityFilterChain filterChain(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(authorize -> authorize.requestMatchers(
				ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
						.concat(ProjectURLConstants.URL_INITIAL_TEMPLATE),
				ProjectURLConstants.URL_STATIC_RESOURCE, SLASH.concat(ProjectURLConstants.URL_HOMEPAGE1),
				SLASH.concat(ProjectURLConstants.URL_HOMEPAGE2), SLASH.concat(ProjectURLConstants.URL_HOMEPAGE3),
				ProjectURLConstants.URL_HOMEPAGE4, SLASH.concat(ProjectURLConstants.URL_HOMEPAGE5),
				ProjectURLConstants.URL_HYMNS_NAMESPACE.concat(SLASH).concat(ProjectURLConstants.URL_COMMON_RETRIEVE),
				ProjectURLConstants.URL_HYMNS_NAMESPACE.concat(SLASH).concat(ProjectURLConstants.URL_SCORE_DOWNLOAD),
				ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
						.concat(ProjectURLConstants.URL_TO_LOGIN_WITH_ERROR),
				ProjectURLConstants.URL_STUDENTS_NAMESPACE.concat(SLASH).concat(ProjectURLConstants.URL_PRE_LOGIN))
				.permitAll()
				.requestMatchers(ProjectURLConstants.URL_HYMNS_NAMESPACE.concat(SLASH)
						.concat(ProjectURLConstants.URL_TO_EDITION))
				.hasAuthority("hymns%edition")
				.requestMatchers(ProjectURLConstants.URL_HYMNS_NAMESPACE.concat(SLASH)
						.concat(ProjectURLConstants.URL_CHECK_DELETE))
				.hasAuthority("hymns%deletion")
				.requestMatchers(ProjectURLConstants.URL_STUDENTS_NAMESPACE.concat(SLASH)
						.concat(ProjectURLConstants.URL_TO_EDITION))
				.hasAuthority("students%retrievEdition").anyRequest().authenticated())
				.csrf(csrf -> csrf.ignoringRequestMatchers(ProjectURLConstants.URL_STATIC_RESOURCE,
						ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH).concat(ProjectURLConstants.URL_LOGIN),
						ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_LOG_OUT))
						.csrfTokenRepository(new CookieCsrfTokenRepository()))
				.exceptionHandling(handling -> {
					handling.authenticationEntryPoint(this.projectAuthenticationEntryPoint);
					handling.accessDeniedHandler((request, response, accessDeniedException) -> {
						response.sendError(HttpStatus.FORBIDDEN.value(),
								ProjectConstants.MESSAGE_SPRINGSECURITY_REQUIRED_AUTH);
						log.warn(ProjectConstants.MESSAGE_SPRINGSECURITY_REQUIRED_AUTH);
					});
				})
				.formLogin(formLogin -> formLogin
						.loginPage(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_TO_LOGIN))
						.loginProcessingUrl(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_LOGIN))
						.defaultSuccessUrl(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_TO_MAINMENU))
						.permitAll().usernameParameter("loginAcct").passwordParameter("userPswd"))
				.logout(logout -> logout
						.logoutUrl(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_LOG_OUT))
						.logoutSuccessUrl(ProjectURLConstants.URL_CATEGORY_NAMESPACE.concat(SLASH)
								.concat(ProjectURLConstants.URL_TO_LOGIN)));
		log.info(ProjectConstants.MESSAGE_SPRING_SECURITY);
		return httpSecurity.build();
	}

}
