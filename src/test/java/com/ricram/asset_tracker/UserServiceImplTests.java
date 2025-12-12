package com.ricram.asset_tracker;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.dto.UserInfoDto;
import com.ricram.asset_tracker.entity.User;
import com.ricram.asset_tracker.repository.UserRepository;
import com.ricram.asset_tracker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("createUser() -> Conflict when email already exists")
    void createUserWithDuplicateEmail() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        CreateUserReqDto req = new CreateUserReqDto(email, "testpw123");

        // Act & assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.createUser(req)
        );
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("already in use"));

        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("createUser() -> Success when email is new, return correct DTO")
    void createUserWithNewEmail() {
        // Arrange
        String email = "test@example.com";
        UUID randomId = UUID.randomUUID();
        when(userRepository.existsByEmail(email)).thenReturn(false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture()))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(randomId);
                    u.setCreatedAt(Instant.parse("2025-12-04T12:00:00Z"));
                    return u;
                });
        CreateUserReqDto req = new CreateUserReqDto(email, "testpw123");

        // Act
        CreateUserRespDto resp = userService.createUser(req);

        // Assert
        assertEquals(randomId, resp.id());
        assertEquals(email, resp.email());

        User savedUser = captor.getValue();
        assertEquals(email, savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(savedUser);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("getUserInfo() -> User not found")
    void getUserInfoWithNonExistentUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserInfo(userId)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("user not found"));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("getUserInfo() -> Success when user exists")
    void getUserInfoSuccessfully() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId,
                "test@email.com",
                "passhash",
                Instant.parse("2025-12-04T12:00:00Z"),
                Instant.parse("2025-12-04T12:00:00Z"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserInfoDto resp = userService.getUserInfo(userId);

        assertEquals(existingUser.getEmail(), resp.email());
        assertEquals(existingUser.getCreatedAt(), resp.createdAt());
        assertEquals(existingUser.getUpdatedAt(), resp.updatedAt());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}
