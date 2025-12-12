package com.ricram.asset_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import com.ricram.asset_tracker.controller.UserController;
import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.dto.UserInfoDto;
import com.ricram.asset_tracker.entity.User;
import com.ricram.asset_tracker.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /users -> 400 when email is missing")
    void whenEmailMissing() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 400 when email present but blank")
    void whenEmailPresentButBlank() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 400 when email format is invalid")
    void whenEmailIsBadFormat() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"not-an-email\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 400 when email is null")
    void whenEmailIsNull() throws Exception {
        // payload with explicit null value
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": null }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 201 Created with location and body when email is valid")
    void whenEmailIsValid() throws Exception {

        // arrange
        String email = "test@example.com";
        UUID randomId = UUID.randomUUID();
        CreateUserRespDto dto = new CreateUserRespDto(randomId, email);
        when(userService.createUser(any(CreateUserReqDto.class))).thenReturn(dto);

        String body = objectMapper.writeValueAsString(new CreateUserReqDto("test@example.com",
                "testing123"));


        // act & assert
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/users/" + randomId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(randomId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /users -> 409 Conflict when email already exists")
    void whenEmailIsDuplicate() throws Exception {

        // arrange: service throws 409
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use: test@example.com"))
                .when(userService).createUser(any(CreateUserReqDto.class));

        String body = objectMapper.writeValueAsString(new CreateUserReqDto("test@example.com",
                "testing123"));

        // act & assert
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /users -> 400 when password is missing")
    void whenPasswordIsMissing() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"testing@email.com\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 400 when password present but blank")
    void whenPasswordPresentButBlank() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"testing@email.com\" " +
                                "\"password\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users -> 400 when password to short")
    void whenPasswordToShort() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"testing@email.com\" " +
                                "\"password\": \"short\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/{userId} -> 404 when user does not exist")
    void whenNonExistentUser() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserInfo(eq(userId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        mvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());

        verify(userService).getUserInfo(eq(userId));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("GET /users/{userId} -> 200 when user exists")
    void whenUserExists() throws Exception {
        UUID userId = UUID.randomUUID();
        UserInfoDto userInfo = new UserInfoDto("test@email.com",
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        when(userService.getUserInfo(eq(userId))).thenReturn(userInfo);

        mvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(userInfo.email()))
                .andExpect(jsonPath("$.createdAt").value(userInfo.createdAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(userInfo.updatedAt().toString()));

        verify(userService).getUserInfo(eq(userId));
        verifyNoMoreInteractions(userService);
    }
}
