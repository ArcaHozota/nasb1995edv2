package app.preach.gospel.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.HymnsWork;

/**
 * 賛美歌セリフ情報リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(HymnsWork.class)
public interface HymnsWorkDao {
}
