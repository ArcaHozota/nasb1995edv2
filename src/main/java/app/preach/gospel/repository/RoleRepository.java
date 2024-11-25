package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.Role;

/**
 * 役割管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
}
