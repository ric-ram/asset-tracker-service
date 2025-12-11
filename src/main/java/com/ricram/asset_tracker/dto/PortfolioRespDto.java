package com.ricram.asset_tracker.dto;

import java.time.Instant;
import java.util.UUID;

public record PortfolioRespDto(
       UUID id,
       String name,
       String description,
       String baseCurrency,
       boolean isArchived,
       Instant createdAt,
       Instant updatedAt
) {
}
