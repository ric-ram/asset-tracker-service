package com.ricram.asset_tracker.controller;

import com.ricram.asset_tracker.dto.CreatePortfolioReqDto;
import com.ricram.asset_tracker.dto.PortfolioRespDto;
import com.ricram.asset_tracker.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioRespDto> createPortfolio(@PathVariable UUID userId, @Valid @RequestBody CreatePortfolioReqDto req) {
        PortfolioRespDto resp = portfolioService.createPortfolioForUser(userId, req);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{portfolioId}")
                .buildAndExpand(resp.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(resp);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioRespDto>> listPortfolios(@PathVariable UUID userId) {
        List<PortfolioRespDto> resp = portfolioService.listPortfoliosForUser(userId);

        return ResponseEntity
                .ok()
                .body(resp);
    }
}
