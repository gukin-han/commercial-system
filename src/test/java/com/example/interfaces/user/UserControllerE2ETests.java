package com.example.interfaces.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.domain.user.Gender;
import com.example.interfaces.user.dto.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerE2ETests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @DisplayName("회원 가입 성공 시 생성된 유저 정보를 반환한다")
  @Test
  void register_shouldReturnCreatedUser() throws Exception {
    UserCreateRequest request =
        new UserCreateRequest("e2euser", "e2e@example.com", "1990-01-01", Gender.MALE);

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.loginId").value("e2euser"))
        .andExpect(jsonPath("$.email").value("e2e@example.com"))
        .andExpect(jsonPath("$.gender").value("MALE"))
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
  }

  @DisplayName("성별이 없으면 400 Bad Request 를 반환한다")
  @Test
  void register_shouldReturnBadRequest_whenGenderMissing() throws Exception {
    String payload =
        objectMapper.writeValueAsString(
            Map.of(
                "loginId", "e2euser2",
                "email", "nogender@example.com",
                "birthDate", "1991-02-02"));

    mockMvc
        .perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest());
  }
}
