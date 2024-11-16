package app.preach.gospel.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import app.preach.gospel.entity.Chapter;

/**
 * 章節リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Chapter.class)
public interface ChapterDao {
}
