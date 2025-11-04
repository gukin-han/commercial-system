package com.example.domain.user;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import com.example.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA 기본 생성자
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
      @UniqueConstraint(name = "uk_user_login_id", columnNames = "login_id")
    })
@Getter
public class User extends BaseEntity {

  private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,10}$");
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  @Column(name = "login_id", nullable = false, length = 10)
  private String loginId;

  @Column(nullable = false, length = 255)
  private String email;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(nullable = false)
  private LocalDate birthDate;

  private User(String loginId, String email, LocalDate birthDate, Gender gender) {
    this.loginId = loginId;
    this.email = email;
    this.birthDate = birthDate;
    this.gender = gender;
  }

  public static User create(String loginId, String email, String birthDate, Gender gender) {
    validateLoginId(loginId);
    validateEmail(email);
    LocalDate parsedBirthDate = parseBirthDate(birthDate);
    return new User(loginId, email, parsedBirthDate, gender);
  }

  private static void validateLoginId(String loginId) {
    Objects.requireNonNull(loginId, "로그인 ID는 필수입니다.");
    if (!LOGIN_ID_PATTERN.matcher(loginId).matches()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "로그인 ID는 영문과 숫자 조합 10자 이하여야 합니다.");
    }
  }

  private static void validateEmail(String email) {
    Objects.requireNonNull(email, "이메일은 필수입니다.");
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다. (예: xx@yy.zz)");
    }
  }

  private static LocalDate parseBirthDate(String birthDate) {
    Objects.requireNonNull(birthDate, "생년월일은 필수입니다.");
    try {
      return LocalDate.parse(birthDate, BIRTH_DATE_FORMATTER);
    } catch (DateTimeParseException ex) {
      throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
    }
  }
}
