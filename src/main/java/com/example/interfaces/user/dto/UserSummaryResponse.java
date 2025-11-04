package com.example.interfaces.user.dto;

import com.example.domain.user.Gender;
import com.example.domain.user.User;

public record UserSummaryResponse(
        Long id,
        String loginId,
        String email,
        Gender gender) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getGender());
    }
}
