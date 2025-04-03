package app.preach.gospel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import app.preach.gospel.common.ProjectConstants;
import lombok.extern.log4j.Log4j2;

/**
 * プロジェクトアプリケーション
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Log4j2
@SpringBootApplication(scanBasePackages = "app.preach.gospel")
@ServletComponentScan
public class NASB1995Application2 {
	public static void main(final String[] args) {
		SpringApplication.run(NASB1995Application2.class, args);
		log.info(ProjectConstants.MESSAGE_SPRING_APPLICATION);
	}
}
