package com.example.interfaces.user.dto;

import com.example.domain.user.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Pattern(
        regexp = "^[A-Za-z0-9]{1,10}$",
        message = "로그인 ID는 영문과 숫자 조합 10자 이하여야 합니다.")
    String loginId,
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,
    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
    String birthDate,
    @NotNull(message = "성별은 필수입니다.")
    Gender gender) {

}
