package com.ricram.asset_tracker.service.impl;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.dto.UserInfoDto;
import com.ricram.asset_tracker.entity.User;
import com.ricram.asset_tracker.repository.UserRepository;
import com.ricram.asset_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CreateUserRespDto createUser(CreateUserReqDto userReqDto) {
        if (userRepository.existsByEmail(userReqDto.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already in use: " + userReqDto.email()
            );
        }

        String passwordHash = passwordEncoder.encode(userReqDto.password());
        User newUser = User.builder()
                .email(userReqDto.email())
                .passwordHash(passwordHash)
                .build();

        User savedUser = userRepository.save(newUser);
        return new CreateUserRespDto(
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(UUID userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        return new UserInfoDto(
                currentUser.getEmail(),
                currentUser.getCreatedAt(),
                currentUser.getUpdatedAt()
        );
    }
}
