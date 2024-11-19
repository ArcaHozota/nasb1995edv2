package app.preach.gospel.service.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.jdbi.v3.core.JdbiException;
import org.jdbi.v3.core.collector.NoSuchCollectorException;
import org.jetbrains.annotations.NotNull;
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
	private final StudentDao studentDao;

	@Override
	public CoResult<Integer, JdbiException> checkDuplicated(final String id, final String loginAccount) {
		try {
			if (CommonProjectUtils.isDigital(id)) {
				final Integer countDuplicated = this.studentDao.countDuplicated(Long.parseLong(id), loginAccount);
				return CoResult.ok(countDuplicated);
			}
			final Integer countDuplicated = this.studentDao.countDuplicated(null, loginAccount);
			return CoResult.ok(countDuplicated);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<StudentDto, JdbiException> getStudentInfoById(final Long id) {
		try {
			final Student student = this.studentDao.selectById(id);
			final StudentDto studentDto = new StudentDto(student.getId().toString(), student.getLoginAccount(),
					student.getUsername(), student.getPassword(), student.getEmail(),
					FORMATTER.format(student.getDateOfBirth()), null);
			return CoResult.ok(studentDto);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<String, JdbiException> infoUpdation(final @NotNull StudentDto studentDto) {
		final Student originalEntity = new Student();
		try {
			final Student student = this.studentDao.selectById(Long.parseLong(studentDto.id()));
			SecondBeanUtils.copyNullableProperties(student, originalEntity);
			SecondBeanUtils.copyNullableProperties(studentDto, student);
			student.setDateOfBirth(LocalDate.parse(studentDto.dateOfBirth(), FORMATTER));
			final String rawPassword = student.getPassword();
			final String password = originalEntity.getPassword();
			student.setPassword(null);
			originalEntity.setPassword(null);
			boolean passwordDiscernment = false;
			if (CommonProjectUtils.isEqual(rawPassword, password)) {
				passwordDiscernment = true;
			} else {
				passwordDiscernment = ENCODER.matches(rawPassword, password);
			}
			if (CommonProjectUtils.isEqual(student, originalEntity) && passwordDiscernment) {
				return CoResult.err(new NoSuchCollectorException(ProjectConstants.MESSAGE_STRING_NO_CHANGE));
			}
			if (passwordDiscernment) {
				student.setPassword(password);
			} else {
				student.setPassword(ENCODER.encode(rawPassword));
			}
			this.studentDao.updateOne(student);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

	@Override
	public CoResult<String, JdbiException> preLoginUpdation(final String loginAccount, final String password) {
		final Student studentEntity = new Student();
		studentEntity.setLoginAccount(loginAccount);
		studentEntity.setEmail(loginAccount);
		try {
			final Student student = this.studentDao.selectOne(studentEntity);
			if (student == null) {
				return CoResult.err(new NoSuchCollectorException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR1));
			}
			final boolean passwordMatches = ENCODER.matches(password, student.getPassword());
			if (!passwordMatches) {
				return CoResult.err(new NoSuchCollectorException(ProjectConstants.MESSAGE_SPRINGSECURITY_LOGINERROR4));
			}
			student.setUpdatedTime(OffsetDateTime.now());
			this.studentDao.updateOne(student);
			return CoResult.ok(ProjectConstants.MESSAGE_STRING_LOGIN_SUCCESS);
		} catch (final JdbiException e) {
			return CoResult.err(e);
		}
	}

}
