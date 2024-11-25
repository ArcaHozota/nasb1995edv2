package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.Phrase;

/**
 * 節別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface PhraseRepository extends JpaRepository<Phrase, Long>, JpaSpecificationExecutor<Phrase> {
}
