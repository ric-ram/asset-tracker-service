package com.ricram.asset_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricram.asset_tracker.controller.UserController;
import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
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

import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
}
