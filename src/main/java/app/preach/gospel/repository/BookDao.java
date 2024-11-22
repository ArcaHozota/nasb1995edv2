package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.Book;

/**
 * 書別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(Book.class)
public interface BookDao {

	/**
	 * 全件検索する
	 *
	 * @param ids IDリスト
	 * @return List<Book>
	 */
	@SqlQuery("select * from books as bk order by bk.id asc")
	List<Book> findAll();
}
