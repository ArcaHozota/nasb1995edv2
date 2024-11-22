package app.preach.gospel.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.springframework.stereotype.Repository;

import app.preach.gospel.entity.Role;

/**
 * 役割管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Repository
@RegisterBeanMapper(Role.class)
public interface RoleDao {
}
