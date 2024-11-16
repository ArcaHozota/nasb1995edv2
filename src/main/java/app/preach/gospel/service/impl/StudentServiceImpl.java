package app.preach.gospel.service.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.HibernateException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.entity.Student;
import app.preach.gospel.repository.StudentDao;
import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoResult;
import app.preach.gospel.utils.CommonProjectUtils;
import app.preach.gospel.utils.SecondBeanUtils;
import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 奉仕者サービス実装クラス
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudentServiceImpl implements IStudentService {

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * 共通検索条件
	 */
	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(BCryptVersion.$2A, 7);

	/**
	 * 奉仕者管理リポジトリ
	 */
	private final StudentDao studentRepository;

	@Override
	public CoResult<Integer, PersistenceException> checkDuplicated(final String id, final String loginAccount) {
		Specification<Student> specification;
		if (CommonProjectUtils.isDigital(id)) {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.notEqual(root.get("id"), Long.parseLong(id)),
						criteriaBuilder.equal(root.get("loginAccount"), loginAccount));
			};
		} else {
			specification = (root, query, criteriaBuilder) -> {
				criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
				return criteriaBuilder.and(criteriaBuilder.equal(root.get("loginAccount"), loginAccount));
			};
		}
		try {
			final long duplicated = this.studentRepository.count(specification);
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<StudentDto, PersistenceException> getStudentInfoById(final Long id) {
		final Specification<Student> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), id));
		};
		final CoResult<StudentDto, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(specification).ifPresentOrElse(val -> {
			final StudentDto studentDto = new StudentDto(val.getId().toString(), val.getLoginAccount(),
					val.getUsername(), val.getPassword(), val.getEmail(), FORMATTER.format(val.getDateOfBirth()), null);
			result.setSelf(CoResult.ok(studentDto));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<String, PersistenceException> infoUpdation(final StudentDto studentDto) {
		final Student student = new Student();
		SecondBeanUtils.copyNullableProperties(studentDto, student);
		student.setId(Long.parseLong(studentDto.id()));
		student.setDateOfBirth(LocalDate.parse(studentDto.dateOfBirth(), FORMATTER));
		student.setVisibleFlg(Boolean.TRUE);
		final Specification<Student> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), student.getId()));
		};
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(specification).ifPresentOrElse(val -> {
			final String rawPassword = student.getPassword();
			final String password = val.getPassword();
			final OffsetDateTime updatedTime = val.getUpdatedTime();
			student.setPassword(null);
			val.setPassword(null);
			val.setUpdatedTime(null);
			boolean passwordDiscernment = false;
			if (CommonProjectUtils.isEqual(rawPassword, password)) {
				passwordDiscernment = true;
			} else {
				passwordDiscernment = ENCODER.matches(rawPassword, password);
			}
			if (CommonProjectUtils.isEqual(student, val) && passwordDiscernment) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				SecondBeanUtils.copyNullableProperties(student, val);
				if (passwordDiscernment) {
					val.setPassword(password);
				} else {
					val.setPassword(ENCODER.encode(rawPassword));
				}
				val.setUpdatedTime(updatedTime);
				try {
					this.studentRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_UPDATED));
				} catch (final PersistenceException e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public CoResult<String, PersistenceException> preLoginUpdation(final String loginAccount, final String password) {
		final Specification<Student> specification = (root, query, criteriaBuilder) -> {
			criteriaBuilder.equal(root.get("visibleFlg"), Boolean.TRUE);
			return criteriaBuilder.or(criteriaBuilder.equal(root.get("loginAccount"), loginAccount),
					criteriaBuilder.equal(root.get("email"), loginAccount));
		};
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(specification).ifPresentOrElse(val -> {
			final boolean passwordMatches = ENCODER.matches(password, val.getPassword());
			if (!passwordMatches) {
				result.setSelf(
						CoResult.err(new HibernateException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR4)));
			} else {
				val.setUpdatedTime(OffsetDateTime.now());
				try {
					this.studentRepository.saveAndFlush(val);
					result.setSelf(CoResult.ok(ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS));
				} catch (final PersistenceException e) {
					result.setSelf(CoResult.err(e));
				}
			}
		}, () -> result
				.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1))));
		return result;
	}

}
