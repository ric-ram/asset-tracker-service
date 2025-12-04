package com.ricram.asset_tracker.dto;

import jakarta.validation.constraints.Email;
import java.util.UUID;

public record CreateUserRespDto(
        UUID id,
        @Email
        String email
) {
}
