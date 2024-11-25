package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.HymnsWork;

/**
 * 賛美歌セリフ情報リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface HymnsWorkRepository extends JpaRepository<HymnsWork, Long>, JpaSpecificationExecutor<HymnsWork> {
}
