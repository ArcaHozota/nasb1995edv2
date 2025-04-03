package app.preach.gospel.config;

import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
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
	@Bean
	@Order(10)
	protected StrutsPrepareAndExecuteFilter strutsPrepareAndExecuteFilter() {
		log.info("Struts2フレームワーク配置成功！");
		return new StrutsPrepareAndExecuteFilter();
	}

}
