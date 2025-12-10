package com.ricram.asset_tracker.service;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.CreatePortfolioRespDto;

import java.util.List;
import java.util.UUID;

/**
 * Defines portfolio-related business operations
 */
public interface PortfolioService {
    /**
     * Creates a new Portfolio for user
     *
     * @param userId user ID of the owner of the portfolio
     * @param portfolioReqDto payload containing the portfolio information
     * @return the recently created portfolio
     */
    CreatePortfolioRespDto createPortfolioForUser(UUID userId, CreatePortfolioReqDto portfolioReqDto);

    /**
     * Lists all the portfolios belonging to a user
     *
     * @param userId user ID of the owner of the portfolios
     * @return a list containing all the portfolios belonging to the user
     */
    List<CreatePortfolioRespDto> listPortfoliosForUser(UUID userId);
}
