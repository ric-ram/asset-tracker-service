package com.ricram.asset_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePortfolioReqDto(
        @NotBlank
        @Size(max = 255, message = "Name should not have more than 255 characters")
        String name,

        String description,

        @Size(min = 3, max = 3, message = "Base currency should contain only 3 characters")
        String baseCurrency
) {
    public CreatePortfolioReqDto {
        if (baseCurrency == null || baseCurrency.isBlank()) {
            baseCurrency = "USD";
        } else {
            baseCurrency = baseCurrency.toUpperCase();
        }
    }
}
