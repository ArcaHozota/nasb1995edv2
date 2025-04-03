package app.preach.gospel.config;

import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.extern.log4j.Log4j2;

/**
 * Struts2設定クラス
 *
 * @author ArkamaHozota
 * @version
 */
@Log4j2
@Configuration
public class ServletFilterConfiguration {

	/**
	 * Strutsの基本フィルタを配置する
	 *
	 * @return StrutsPrepareAndExecuteFilter
	 */
	@Order(10)
	@Bean
	protected FilterRegistrationBean<StrutsPrepareAndExecuteFilter> struts2Filter() {
		final FilterRegistrationBean<StrutsPrepareAndExecuteFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(new StrutsPrepareAndExecuteFilter());
		filter.addUrlPatterns("*.action"); // ⚠️ 这是会拦截所有请求，包括静态资源
		log.info("Struts2フレームワーク配置成功！");
		return filter;
	}

}
