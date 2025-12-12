package com.ricram.asset_tracker.service;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;
import com.ricram.asset_tracker.dto.UserInfoDto;

import java.util.UUID;

/**
 * Defines user-related business operations
 */
public interface UserService {
    /**
     * Creates a new User
     *
     * @param userReqDto payload containing the user information
     * @return the recently created user
     */
    CreateUserRespDto createUser(CreateUserReqDto userReqDto);

    /**
     * Gets user details
     *
     * @param userId the user id
     * @return user details
     */
    UserInfoDto getUserInfo(UUID userId);
}
