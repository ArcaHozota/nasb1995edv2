package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.StudentRole;

/**
 * 奉仕者役割連携リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface StudentRoleRepository extends JpaRepository<StudentRole, Long>, JpaSpecificationExecutor<StudentRole> {
}
