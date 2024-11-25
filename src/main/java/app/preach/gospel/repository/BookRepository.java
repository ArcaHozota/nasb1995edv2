package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.Book;

/**
 * 書別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface BookRepository extends JpaRepository<Book, Short>, JpaSpecificationExecutor<Book> {
}
