package com.example.service.user;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import com.example.domain.user.Gender;
import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public User register(String loginId, String email, String birthDate, Gender gender) {
    User user = User.create(loginId, email, birthDate, gender);

    if (userRepository.existsByLoginId(user.getLoginId())) {
      throw new CoreException(ErrorType.CONFLICT, "이미 사용 중인 로그인 ID입니다.");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new CoreException(ErrorType.CONFLICT, "이미 가입된 이메일입니다.");
    }

    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public User getByLoginId(String loginId) {
    return userRepository
        .findByLoginId(loginId)
        .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
  }
}
