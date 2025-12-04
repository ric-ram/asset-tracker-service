package com.ricram.asset_tracker.service;

import com.ricram.asset_tracker.dto.CreateUserReqDto;
import com.ricram.asset_tracker.dto.CreateUserRespDto;

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
}
