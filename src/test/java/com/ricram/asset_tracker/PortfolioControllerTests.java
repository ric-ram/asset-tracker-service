package com.ricram.asset_tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricram.asset_tracker.controller.PortfolioController;
import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.PortfolioRespDto;
import com.ricram.asset_tracker.service.PortfolioService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PortfolioControllerTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    @DisplayName("POST /users/{userId}/portfolios -> 404 when user is not found")
    void whenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(portfolioService.createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        mvc.perform(post("/users/" + userId + "/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"test\" }"))
                .andExpect(status().isNotFound());

        verify(portfolioService).createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class));
    }

    @Test
    @DisplayName("POST /users/{userId}/portfolios -> 400 when body is invalid")
    void whenInvalidBody() throws Exception {
        UUID userId = UUID.randomUUID();
        mvc.perform(post("/users/" + userId + "/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users/{userId}/portfolios -> 201 Created with base currency provided")
    void whenBaseCurrencyProvided() throws Exception {
        // arrange
        UUID userId = UUID.randomUUID();
        UUID portfolioID = UUID.randomUUID();
        PortfolioRespDto resp = new PortfolioRespDto(
                portfolioID,
                "test",
                null,
                "USD",
                false,
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        when(portfolioService.createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class))).thenReturn(resp);

        String body = objectMapper.writeValueAsString(new CreatePortfolioReqDto("test",
                null,
                "USD"));

        mvc.perform(post("/users/" + userId + "/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/users/" + userId + "/portfolios/" + portfolioID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(portfolioID.toString()))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.baseCurrency").value("USD"));

        verify(portfolioService).createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class));
    }

    @Test
    @DisplayName("POST /users/{userId}/portfolios -> 201 Created with no base currency provided")
    void whenNoBaseCurrencyProvided() throws Exception {
        // arrange
        UUID userId = UUID.randomUUID();
        UUID portfolioID = UUID.randomUUID();
        PortfolioRespDto resp = new PortfolioRespDto(
                portfolioID,
                "test",
                null,
                "USD",
                false,
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        when(portfolioService.createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class))).thenReturn(resp);

        String body = """
                      {
                        "name": "test"
                      }
                      """;

        mvc.perform(post("/users/" + userId + "/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/users/" + userId + "/portfolios/" + portfolioID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(portfolioID.toString()))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.baseCurrency").value("USD"));

        verify(portfolioService).createPortfolioForUser(eq(userId), any(CreatePortfolioReqDto.class));
    }
}
