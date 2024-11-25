package app.preach.gospel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.preach.gospel.entity.Student;

/**
 * 奉仕者管理リポジトリ
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
}
