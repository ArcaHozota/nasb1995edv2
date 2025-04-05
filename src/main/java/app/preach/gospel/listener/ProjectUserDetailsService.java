package app.preach.gospel.listener;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.entity.Authority;
import app.preach.gospel.entity.RoleAuthority;
import app.preach.gospel.entity.Student;
import app.preach.gospel.entity.StudentRole;
import app.preach.gospel.repository.AuthorityRepository;
import app.preach.gospel.repository.RoleAuthorityRepository;
import app.preach.gospel.repository.StudentRepository;
import app.preach.gospel.repository.StudentRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * ログインコントローラ(SpringSecurity関連)
 *
 * @author ArkamaHozota
 * @since 2.00
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectUserDetailsService implements UserDetailsService {

	/**
	 * 権限管理リポジトリ
	 */
	private final AuthorityRepository authorityRepository;

	/**
	 * 役割権限連携リポジトリ
	 */
	private final RoleAuthorityRepository roleAuthorityRepository;

	/**
	 * 奉仕者管理リポジトリ
	 */
	private final StudentRepository studentRepository;

	/**
	 * 奉仕者役割連携リポジトリ
	 */
	private final StudentRoleRepository studentRoleRepository;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final Specification<Student> specification1 = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.or(criteriaBuilder.equal(root.get("loginAccount"), username),
					criteriaBuilder.equal(root.get("email"), username));
		};
		final Optional<Student> studentOptional = this.studentRepository.findOne(specification1);
		if (studentOptional.isEmpty()) {
			throw new DisabledException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1);
		}
		final Student student = studentOptional.get();
		final Optional<StudentRole> studentRoleOptional = this.studentRoleRepository.findById(student.getId());
		if (studentRoleOptional.isEmpty()) {
			throw new InsufficientAuthenticationException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR2);
		}
		final Long roleId = studentRoleOptional.get().getRoleId();
		final Specification<RoleAuthority> specification2 = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("roleId"), roleId);
		final List<Long> authIds = this.roleAuthorityRepository.findAll(specification2).stream()
				.map(RoleAuthority::getAuthId).toList();
		if (CollectionUtils.isEmpty(authIds)) {
			throw new AuthenticationCredentialsNotFoundException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR3);
		}
		final List<Authority> authoritiesRecords = this.authorityRepository.findAllById(authIds);
		final StudentDto studentDto = new StudentDto(student.getId().toString(), student.getLoginAccount(),
				student.getUsername(), student.getPassword(), student.getEmail(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd").format(student.getDateOfBirth()), roleId.toString());
		final List<SimpleGrantedAuthority> authorities = authoritiesRecords.stream()
				.map(item -> new SimpleGrantedAuthority(item.getName())).toList();
		return new SecurityAdmin(studentDto, authorities);
	}

}
