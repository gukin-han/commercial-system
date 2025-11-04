package com.example.interfaces.user;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import com.example.interfaces.user.dto.UserCreateRequest;
import com.example.interfaces.user.dto.UserCreateResponse;
import com.example.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private static final String USER_ID_HEADER = "X-USER-ID";

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserCreateResponse register(@Valid @RequestBody UserCreateRequest request) {
    return UserCreateResponse.from(
        userService.register(request.loginId(), request.email(), request.birthDate(), request.gender()));
  }

  @GetMapping("/me")
  public UserCreateResponse getMe(@RequestHeader(USER_ID_HEADER) String loginId) {
    if (!StringUtils.hasText(loginId)) {
      throw new CoreException(ErrorType.BAD_REQUEST, "X-USER-ID 헤더는 필수입니다.");
    }

    return UserCreateResponse.from(userService.getByLoginId(loginId));
  }
}
