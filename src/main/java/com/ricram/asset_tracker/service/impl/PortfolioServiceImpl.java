package com.ricram.asset_tracker.service.impl;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.CreatePortfolioRespDto;
import com.ricram.asset_tracker.service.PortfolioService;

import java.util.List;
import java.util.UUID;

public class PortfolioServiceImpl implements PortfolioService {
    @Override
    public CreatePortfolioRespDto createPortfolioForUser(UUID userId, CreatePortfolioReqDto portfolioReqDto) {
        return null;
    }

    @Override
    public List<CreatePortfolioRespDto> listPortfoliosForUser(UUID userId) {
        return null;
    }
}
