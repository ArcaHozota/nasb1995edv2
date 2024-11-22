package app.preach.gospel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import app.preach.gospel.common.ProjectConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * プロジェクトアプリケーション
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = { "app.preach.gospel.repository", "app.preach.gospel.service",
		"app.preach.gospel.service.impl", "app.preach.gospel.handler" })
public class ProjectApplication {
	public static void main(final String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
		log.info(ProjectConstants.MESSAGE_SPRING_APPLICATION);
	}
}
