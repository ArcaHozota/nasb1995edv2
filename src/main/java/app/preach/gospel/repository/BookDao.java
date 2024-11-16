package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import app.preach.gospel.entity.Book;

/**
 * 書別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
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
