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
import lombok.extern.slf4j.Slf4j;

/**
 * JDBI設定クラス
 *
 * @author ArkamaHozota
 * @version
 */
@Slf4j
@Configuration
public class JDBIConfiguration {

	@Bean
	@Order(3)
	protected Jdbi jdbi(final DataSource dataSource) {
		final Jdbi jdbi = Jdbi.create(dataSource); // 使用 Spring 提供的 DataSource
		jdbi.installPlugin(new SqlObjectPlugin()); // 安装插件，支持 DAO 接口
		jdbi.registerRowMapper(BeanMapper.factory(Authority.class)); // 注册驼峰映射策略
		jdbi.registerRowMapper(BeanMapper.factory(Book.class));
		jdbi.registerRowMapper(BeanMapper.factory(Chapter.class));
		jdbi.registerRowMapper(BeanMapper.factory(Hymn.class));
		jdbi.registerRowMapper(BeanMapper.factory(HymnsWork.class));
		jdbi.registerRowMapper(BeanMapper.factory(Phrase.class));
		jdbi.registerRowMapper(BeanMapper.factory(Role.class));
		jdbi.registerRowMapper(BeanMapper.factory(RoleAuthority.class));
		jdbi.registerRowMapper(BeanMapper.factory(Student.class));
		jdbi.registerRowMapper(BeanMapper.factory(StudentRole.class));
		log.info("JDBIフレームワーク配置成功！");
		return jdbi;
	}

}
