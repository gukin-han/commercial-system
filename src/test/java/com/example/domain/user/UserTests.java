package com.example.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTests {

  private static final String VALID_LOGIN_ID = "user123";
  private static final String VALID_EMAIL = "user@example.com";
  private static final String VALID_BIRTH_DATE = "1990-01-01";
  private static final Gender VALID_GENDER = Gender.MALE;

  @DisplayName("User.create")
  @Nested
  class CreateUser {

    @DisplayName("모든 값이 유효하면 User 생성에 성공한다")
    @Test
    void shouldCreateUser_whenInputIsValid() {
      User user = User.create(VALID_LOGIN_ID, VALID_EMAIL, VALID_BIRTH_DATE, VALID_GENDER);

      assertAll(() -> assertThat(user).isNotNull());
    }

    @DisplayName("로그인 ID가 형식에 맞지 않으면 예외를 던진다")
    @Test
    void shouldThrowCoreException_whenLoginIdIsInvalid() {
      CoreException exception =
          catchThrowableOfType(
              () -> User.create("invalid!", VALID_EMAIL, VALID_BIRTH_DATE, VALID_GENDER),
              CoreException.class);

      assertAll(
          () -> assertThat(exception).isNotNull(),
          () -> assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
          () ->
              assertThat(exception)
                  .hasMessage("로그인 ID는 영문과 숫자 조합 10자 이하여야 합니다."));
    }

    @DisplayName("이메일 형식이 올바르지 않으면 예외를 던진다")
    @Test
    void shouldThrowCoreException_whenEmailIsInvalid() {
      CoreException exception =
          catchThrowableOfType(
              () -> User.create(VALID_LOGIN_ID, "invalid-email", VALID_BIRTH_DATE, VALID_GENDER),
              CoreException.class);

      assertAll(
          () -> assertThat(exception).isNotNull(),
          () -> assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
          () ->
              assertThat(exception)
                  .hasMessage("이메일 형식이 올바르지 않습니다. (예: xx@yy.zz)"));
    }

    @DisplayName("생년월일이 yyyy-MM-dd 형식이 아니면 예외를 던진다")
    @Test
    void shouldThrowCoreException_whenBirthDateIsInvalid() {
      CoreException exception =
          catchThrowableOfType(
              () -> User.create(VALID_LOGIN_ID, VALID_EMAIL, "19900101", VALID_GENDER),
              CoreException.class);

      assertAll(
          () -> assertThat(exception).isNotNull(),
          () -> assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
          () -> assertThat(exception).hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다."));
    }
  }
}
