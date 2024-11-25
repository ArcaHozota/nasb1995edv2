package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.RoleAuthIds;
import app.preach.gospel.entity.RoleAuthority;

/**
 * 役割権限連携リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface RoleAuthorityRepository
		extends JpaRepository<RoleAuthority, RoleAuthIds>, JpaSpecificationExecutor<RoleAuthority> {
}
