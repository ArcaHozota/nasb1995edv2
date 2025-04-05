package app.preach.gospel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.preach.gospel.entity.Book;

/**
 * 書別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface BookRepository extends JpaRepository<Book, Short>, JpaSpecificationExecutor<Book> {

	/**
	 * 書別IDによって章節情報を取得する
	 *
	 * @param id 書別ID
	 * @return Optional<Book>
	 */
	@Query("select bk from Book as bk inner join fetch bk.chapters where bk.id =:id")
	Optional<Book> findByIdWithChapters(@Param("id") Short id);
}
