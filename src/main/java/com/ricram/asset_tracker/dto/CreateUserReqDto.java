package com.ricram.asset_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserReqDto(
        @NotBlank(message = "Email should not be empty")
        @Email
        String email,

        @NotBlank(message = "password should not be empty")
        String password
) {
}
