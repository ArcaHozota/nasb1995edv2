package app.preach.gospel.service.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.HibernateException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.preach.gospel.common.ProjectConstants;
import app.preach.gospel.dto.StudentDto;
import app.preach.gospel.entity.Student;
import app.preach.gospel.repository.StudentRepository;
import app.preach.gospel.service.IStudentService;
import app.preach.gospel.utils.CoBeanUtils;
import app.preach.gospel.utils.CoProjectUtils;
import app.preach.gospel.utils.CoResult;
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
	 * 共通検索条件
	 */
	protected static final Specification<Student> COMMON_CONDITION = (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("visibleFlg"), Boolean.TRUE);

	/**
	 * 日時フォマーター
	 */
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * エンコーダ
	 */
	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(BCryptVersion.$2A, 7);

	/**
	 * アカウント
	 */
	private static final String LOGIN_ACCOUNT = "loginAccount";

	/**
	 * 奉仕者管理リポジトリ
	 */
	private final StudentRepository studentRepository;

	@Override
	public CoResult<Integer, PersistenceException> checkDuplicated(final String id, final String loginAccount) {
		try {
			final Specification<Student> specification = (root, query, criteriaBuilder) -> criteriaBuilder
					.equal(root.get(LOGIN_ACCOUNT), loginAccount);
			if (CoProjectUtils.isDigital(id)) {
				final Specification<Student> specification1 = (root, query, criteriaBuilder) -> criteriaBuilder
						.notEqual(root.get("id"), Long.parseLong(id));
				final long duplicated = this.studentRepository
						.count(COMMON_CONDITION.and(specification).and(specification1));
				return CoResult.ok((int) duplicated);
			}
			final long duplicated = this.studentRepository.count(COMMON_CONDITION.and(specification));
			return CoResult.ok((int) duplicated);
		} catch (final PersistenceException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public @NotNull CoResult<StudentDto, PersistenceException> getStudentInfoById(final Long id) {
		final Specification<Student> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), id);
		final CoResult<StudentDto, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			final StudentDto studentDto = new StudentDto(val.getId().toString(), val.getLoginAccount(),
					val.getUsername(), val.getPassword(), val.getEmail(), FORMATTER.format(val.getDateOfBirth()), null);
			result.setSelf(CoResult.ok(studentDto));
		}, () -> result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_FATAL_ERROR))));
		return result;
	}

	@Override
	public @NotNull CoResult<String, PersistenceException> infoUpdation(final StudentDto studentDto) {
		final Student student = new Student();
		CoBeanUtils.copyNullableProperties(studentDto, student);
		student.setId(Long.parseLong(studentDto.id()));
		student.setDateOfBirth(LocalDate.parse(studentDto.dateOfBirth(), FORMATTER));
		student.setVisibleFlg(Boolean.TRUE);
		final Specification<Student> specification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("id"), student.getId());
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
			final String rawPassword = student.getPassword();
			final String password = val.getPassword();
			final OffsetDateTime updatedTime = val.getUpdatedTime();
			student.setPassword(null);
			val.setPassword(null);
			val.setUpdatedTime(null);
			boolean passwordDiscernment;
			if (CoProjectUtils.isEqual(rawPassword, password)) {
				passwordDiscernment = true;
			} else {
				passwordDiscernment = ENCODER.matches(rawPassword, password);
			}
			if (CoProjectUtils.isEqual(student, val) && passwordDiscernment) {
				result.setSelf(CoResult.err(new HibernateException(ProjectConstants.MESSAGE_STRING_NO_CHANGE)));
			} else {
				CoBeanUtils.copyNullableProperties(student, val);
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
	public @NotNull CoResult<String, PersistenceException> preLoginUpdation(final String loginAccount,
			final String password) {
		final Specification<Student> specification = (root, query, criteriaBuilder) -> criteriaBuilder.or(
				criteriaBuilder.equal(root.get(LOGIN_ACCOUNT), loginAccount),
				criteriaBuilder.equal(root.get("email"), loginAccount));
		final CoResult<String, PersistenceException> result = CoResult.getInstance();
		this.studentRepository.findOne(COMMON_CONDITION.and(specification)).ifPresentOrElse(val -> {
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
