package com.ricram.asset_tracker;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.CreatePortfolioRespDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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
    @DisplayName("createPortfolio() -> User not found")
    void createPortfolioWithNotExistentUser() {
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
    @DisplayName("createPortfolio() -> Success when user exists and requestDTO is correct, return correct DTO")
    void createPortfolioSuccessfully() {
        // Arrange
        UUID userID = UUID.randomUUID();
        UUID portfolioID = UUID.randomUUID();
        User existingUser = new User(userID, "test@email.com", "passhash", Instant.parse("2025-12-04T12:00:00Z"), Instant.parse("2025-12-04T12:00:00Z"));
        when(userRepository.findById(userID)).thenReturn(Optional.of(existingUser));

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        when(portfolioRepository.save(captor.capture()))
                .thenAnswer(invocation -> {
                    Portfolio p = invocation.getArgument(0);
                    p.setId(portfolioID);
                    p.setUser(existingUser);
                    p.setCreatedAt(Instant.parse("2025-12-04T12:00:00Z"));
                    return p;
                });
        CreatePortfolioReqDto req = new CreatePortfolioReqDto("test", null, null);

        // Act
        CreatePortfolioRespDto resp = portfolioService.createPortfolioForUser(userID, req);

        // Assert
        assertEquals(portfolioID, resp.id());
        assertEquals("test", resp.name());
        assertEquals("USD", resp.baseCurrency());

        Portfolio savedPortfolio = captor.getValue();
        assertEquals("test", savedPortfolio.getName());
        assertEquals(existingUser, savedPortfolio.getUser());
        assertEquals("USD", savedPortfolio.getBaseCurrency());
        assertFalse(resp.isArchived());
        assertNotNull(savedPortfolio.getCreatedAt());

        verify(userRepository).findById(userID);
        verify(portfolioRepository).save(savedPortfolio);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(portfolioRepository);
    }
}
