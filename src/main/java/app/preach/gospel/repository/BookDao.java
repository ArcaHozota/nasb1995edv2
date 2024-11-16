package app.preach.gospel.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import app.preach.gospel.entity.Book;

/**
 * 書別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Book.class)
public interface BookDao {
}
