package com.ricram.asset_tracker.service.impl;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.PortfolioRespDto;
import com.ricram.asset_tracker.entity.Portfolio;
import com.ricram.asset_tracker.entity.User;
import com.ricram.asset_tracker.repository.PortfolioRepository;
import com.ricram.asset_tracker.repository.UserRepository;
import com.ricram.asset_tracker.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final UserRepository userRepository;

    private final PortfolioRepository portfolioRepository;

    private PortfolioRespDto toRespDto(Portfolio portfolio) {
        return new PortfolioRespDto(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getDescription(),
                portfolio.getBaseCurrency(),
                portfolio.isArchived(),
                portfolio.getCreatedAt(),
                portfolio.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public PortfolioRespDto createPortfolioForUser(UUID userId, CreatePortfolioReqDto portfolioReqDto) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        Portfolio newPortfolio = Portfolio.builder()
                .name(portfolioReqDto.name())
                .description(portfolioReqDto.description())
                .user(currentUser)
                .baseCurrency(portfolioReqDto.baseCurrency())
                .build();
        Portfolio savedPortfolio = portfolioRepository.save(newPortfolio);
        return toRespDto(savedPortfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioRespDto> listPortfoliosForUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }


        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId, sort);
        return portfolios.stream().map(this::toRespDto).toList();
    }
}
