package app.preach.gospel.config;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import app.preach.gospel.entity.Authority;
import app.preach.gospel.entity.Book;
import app.preach.gospel.entity.Chapter;
import app.preach.gospel.entity.Hymn;
import app.preach.gospel.entity.HymnsWork;
import app.preach.gospel.entity.Phrase;
import app.preach.gospel.entity.Role;
import app.preach.gospel.entity.RoleAuthority;
import app.preach.gospel.entity.Student;
import app.preach.gospel.entity.StudentRole;
import lombok.extern.log4j.Log4j2;

/**
 * JDBI設定クラス
 *
 * @author ArkamaHozota
 * @version
 */
@Log4j2
@Configuration
public class JDBIConfiguration {

	@Bean
	@Order(3)
	protected Jdbi jdbi(final DataSource dataSource) {
		final Jdbi jdbi = Jdbi.create(dataSource); // 使用 Spring 提供的 DataSource
		jdbi.installPlugin(new SqlObjectPlugin()); // 安装插件，支持 DAO 接口
		jdbi.registerRowMapper(BeanMapper.of(Authority.class)); // 注册驼峰映射策略
		jdbi.registerRowMapper(BeanMapper.of(Book.class));
		jdbi.registerRowMapper(BeanMapper.of(Chapter.class));
		jdbi.registerRowMapper(BeanMapper.of(Hymn.class));
		jdbi.registerRowMapper(BeanMapper.of(HymnsWork.class));
		jdbi.registerRowMapper(BeanMapper.of(Phrase.class));
		jdbi.registerRowMapper(BeanMapper.of(Role.class));
		jdbi.registerRowMapper(BeanMapper.of(RoleAuthority.class));
		jdbi.registerRowMapper(BeanMapper.of(Student.class));
		jdbi.registerRowMapper(BeanMapper.of(StudentRole.class));
		log.info("JDBIフレームワーク配置成功！");
		return jdbi;
	}

}
