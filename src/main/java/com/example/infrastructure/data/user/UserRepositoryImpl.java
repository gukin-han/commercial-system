package com.example.infrastructure.data.user;

import com.example.domain.user.User;
import com.example.domain.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // Component 스캔만으로는 JPA 예외 변환이 적용되지 않아 @Repository 로 명시합니다.
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findByLoginId(String loginId) {
    return userJpaRepository.findByLoginId(loginId);
  }

  @Override
  public boolean existsByLoginId(String loginId) {
    return userJpaRepository.existsByLoginId(loginId);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }
}
