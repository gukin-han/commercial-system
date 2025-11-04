package com.example.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import com.example.domain.user.Gender;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceIntegrationTests {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  @DisplayName("회원 가입 시 User 가 영속화된다")
  @Test
  void register_shouldPersistUser() {
    User user = userService.register("user001", "user001@example.com", "1990-01-01", Gender.MALE);

    assertAll(
        () -> assertThat(user.getId()).isNotNull(),
        () ->
            assertThat(userRepository.findByLoginId("user001"))
                .isPresent()
                .get()
                .extracting(User::getId)
                .isEqualTo(user.getId()));
  }

  @DisplayName("이미 가입된 로그인 ID로 회원가입하면 실패한다")
  @Test
  void register_shouldFail_whenLoginIdDuplicate() {
    userService.register("dupuser1", "first@example.com", "1990-01-01", Gender.FEMALE);

    CoreException exception =
        catchThrowableOfType(
            () -> userService.register("dupuser1", "second@example.com", "1990-01-01", Gender.MALE),
            CoreException.class);

    assertAll(
        () -> assertThat(exception).isNotNull(),
        () -> assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT),
        () -> assertThat(exception).hasMessage("이미 사용 중인 로그인 ID입니다."));
  }
}
