package app.preach.gospel.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import app.preach.gospel.entity.Phrase;

/**
 * 節別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Phrase.class)
public interface PhraseDao {
}
