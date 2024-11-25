package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.Chapter;

/**
 * 章節リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface ChapterRepository extends JpaRepository<Chapter, Integer>, JpaSpecificationExecutor<Chapter> {
}
