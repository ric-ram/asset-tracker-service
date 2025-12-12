package com.ricram.asset_tracker.dto;

import java.time.Instant;

public record UserInfoDto(
        String email,

        Instant createdAt,

        Instant updatedAt
) {
}
