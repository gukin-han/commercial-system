package com.example.interfaces.user.dto;

import com.example.domain.user.Gender;
import com.example.domain.user.User;
import java.time.LocalDate;

public record UserCreateResponse(
        Long id,
        String loginId,
        String email,
        Gender gender,
        LocalDate birthDate) {

    public static UserCreateResponse of(
            Long id,
            String loginId,
            String email,
            Gender gender,
            LocalDate birthDate) {
        return new UserCreateResponse(id, loginId, email, gender, birthDate);
    }

    public static UserCreateResponse from(User user) {
        return new UserCreateResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getGender(),
                user.getBirthDate());
    }
}
