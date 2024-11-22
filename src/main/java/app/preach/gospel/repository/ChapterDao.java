package app.preach.gospel.repository;

import java.util.List;

import org.jdbi.v3.core.result.NoResultsException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.Chapter;

/**
 * 章節リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(Chapter.class)
public interface ChapterDao {

	/**
	 * 書別IDによって検索する
	 *
	 * @param bookId 書別ID
	 * @return List<Chapter>
	 */
	@SqlQuery("select * from chapters as cp where cp.book_id =:bookId")
	List<Chapter> findByBookId(Short bookId);

	/**
	 * IDによって1件検索する
	 *
	 * @param id ID
	 * @return Chapter
	 */
	@SqlQuery("select * from chapters as cp where cp.id =:id")
	Chapter selectById(Integer id) throws NoResultsException;
}
