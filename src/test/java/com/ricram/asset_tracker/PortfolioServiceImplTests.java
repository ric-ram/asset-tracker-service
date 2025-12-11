package com.ricram.asset_tracker;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.PortfolioRespDto;
import com.ricram.asset_tracker.entity.Portfolio;
import com.ricram.asset_tracker.entity.User;
import com.ricram.asset_tracker.repository.PortfolioRepository;
import com.ricram.asset_tracker.repository.UserRepository;
import com.ricram.asset_tracker.service.impl.PortfolioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceImplTests {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    @Test
    @DisplayName("createPortfolioForUser() -> User not found")
    void createPortfolioWithNonExistentUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        CreatePortfolioReqDto req = new CreatePortfolioReqDto("test", null, null);

        // Act & Assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> portfolioService.createPortfolioForUser(userId, req)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("user not found"));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("createPortfolioForUser() -> Success when user exists and requestDTO is correct, return correct DTO")
    void createPortfolioSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID portfolioId = UUID.randomUUID();
        User existingUser = new User(userId,
                "test@email.com",
                "passhash",
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        when(portfolioRepository.save(captor.capture()))
                .thenAnswer(invocation -> {
                    Portfolio p = invocation.getArgument(0);
                    p.setId(portfolioId);
                    p.setUser(existingUser);
                    p.setCreatedAt(Instant.parse("2025-12-04T12:00:00Z"));
                    return p;
                });
        CreatePortfolioReqDto req = new CreatePortfolioReqDto("test", null, null);

        // Act
        PortfolioRespDto resp = portfolioService.createPortfolioForUser(userId, req);

        // Assert
        assertEquals(portfolioId, resp.id());
        assertEquals("test", resp.name());
        assertEquals("USD", resp.baseCurrency());

        Portfolio savedPortfolio = captor.getValue();
        assertEquals("test", savedPortfolio.getName());
        assertEquals(existingUser, savedPortfolio.getUser());
        assertEquals("USD", savedPortfolio.getBaseCurrency());
        assertFalse(resp.isArchived());
        assertNotNull(savedPortfolio.getCreatedAt());

        verify(userRepository).findById(userId);
        verify(portfolioRepository).save(savedPortfolio);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("listPortfoliosForUser() -> User not found")
    void listPortfoliosWithNonExistentUser() {

        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> portfolioService.listPortfoliosForUser(userId)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("user not found"));

        verify(userRepository).existsById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("listPortfoliosForUser -> Success when user has no portfolios")
    void listPortfoliosSuccessfullyEmptyList() {

        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId,
                "test@email.com",
                "passhash",
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        when(userRepository.existsById(userId)).thenReturn(true);

        when(portfolioRepository.findByUserId(userId, sort)).thenReturn(List.of());

        List<PortfolioRespDto> resp = portfolioService.listPortfoliosForUser(userId);

        assertNotNull(resp);
        assertTrue(resp.isEmpty());

        verify(userRepository).existsById(userId);
        verify(portfolioRepository).findByUserId(userId, sort);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("listPortfoliosForUser -> Success when user has portfolios")
    void listPortfoliosSuccessfully() {

        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId,
                "test@email.com",
                "passhash",
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();

        Portfolio p1 = Portfolio.builder()
                .id(p1Id)
                .name("Long term")
                .description("Long term investments")
                .baseCurrency("USD")
                .user(existingUser)
                .isArchived(false)
                .createdAt(Instant.parse("2025-12-04T10:00:00Z"))
                .updatedAt(Instant.parse("2025-12-04T10:00:00Z"))
                .build();
        Portfolio p2 = Portfolio.builder()
                .id(p2Id)
                .name("Short term")
                .description("Short term trades")
                .baseCurrency("EUR")
                .user(existingUser)
                .isArchived(true)
                .createdAt(Instant.parse("2025-12-03T10:00:00Z"))
                .updatedAt(Instant.parse("2025-12-03T10:00:00Z"))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);

        when(portfolioRepository.findByUserId(userId, sort)).thenReturn(List.of(p1, p2));

        List<PortfolioRespDto> resp = portfolioService.listPortfoliosForUser(userId);

        assertNotNull(resp);
        assertEquals(2, resp.size());

        PortfolioRespDto first = resp.getFirst();
        assertEquals(p1.getId(), first.id());
        assertEquals("Long term", first.name());
        assertEquals("USD", first.baseCurrency());
        assertFalse(first.isArchived());

        PortfolioRespDto second = resp.get(1);
        assertEquals(p2.getId(), second.id());
        assertEquals("Short term", second.name());
        assertEquals("EUR", second.baseCurrency());
        assertTrue(second.isArchived());

        assertTrue(first.createdAt().isAfter(second.createdAt()));

        verify(userRepository).existsById(userId);
        verify(portfolioRepository).findByUserId(userId, sort);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }
}
