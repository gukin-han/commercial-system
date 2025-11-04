package com.example.domain.user;

import java.util.Optional;

public interface UserRepository {

  User save(User user);

  Optional<User> findByLoginId(String loginId);

  boolean existsByLoginId(String loginId);

  boolean existsByEmail(String email);
}
