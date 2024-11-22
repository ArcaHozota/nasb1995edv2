package app.preach.gospel.listener;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.NoResultsException;
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
import app.preach.gospel.repository.AuthorityDao;
import app.preach.gospel.repository.RoleAuthorityDao;
import app.preach.gospel.repository.StudentDao;
import app.preach.gospel.repository.StudentRoleDao;
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
	 * 共通リポジトリ
	 */
	private final Jdbi jdbi;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final Student studentEntity = new Student();
		studentEntity.setLoginAccount(username);
		studentEntity.setEmail(username);
		final Student student = this.jdbi.onDemand(StudentDao.class).selectOne(studentEntity);
		if (student == null) {
			throw new DisabledException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1);
		}
		StudentRole studentRole;
		try {
			studentRole = this.jdbi.onDemand(StudentRoleDao.class).selectById(student.getId());
		} catch (final NoResultsException e) {
			throw new InsufficientAuthenticationException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR2);
		}
		final List<Long> authIds = this.jdbi.onDemand(RoleAuthorityDao.class).findByRoleId(studentRole.getRoleId())
				.stream().map(RoleAuthority::getAuthId).toList();
		if (CollectionUtils.isEmpty(authIds)) {
			throw new AuthenticationCredentialsNotFoundException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR3);
		}
		final List<Authority> authoritiesRecords = this.jdbi.onDemand(AuthorityDao.class).findByIds(authIds);
		final StudentDto studentDto = new StudentDto(student.getId().toString(), student.getLoginAccount(),
				student.getUsername(), student.getPassword(), student.getEmail(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd").format(student.getDateOfBirth()),
				studentRole.getRoleId().toString());
		final List<SimpleGrantedAuthority> authorities = authoritiesRecords.stream()
				.map(item -> new SimpleGrantedAuthority(item.getName())).collect(Collectors.toList());
		return new SecurityAdmin(studentDto, authorities);
	}

}
