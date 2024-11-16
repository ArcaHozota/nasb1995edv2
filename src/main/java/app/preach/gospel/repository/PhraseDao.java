package app.preach.gospel.repository;

import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import app.preach.gospel.entity.Phrase;

/**
 * 節別リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@RegisterBeanMapper(Phrase.class)
public interface PhraseDao {

	/**
	 * 節別の情報を保存する
	 *
	 * @param phrase 節別エンティティ
	 */
	@SqlUpdate("insert into phrases(id, name, text_en, text_jp, chapter_id, change_line) "
			+ "value(:id, :name, :textEn, :textJp, :chapterId, :changeLine)")
	void insertOne(@BindBean Phrase phrase) throws TransactionException;
}
