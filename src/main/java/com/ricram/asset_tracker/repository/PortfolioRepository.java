package com.ricram.asset_tracker.repository;

import com.ricram.asset_tracker.entity.Portfolio;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    List<Portfolio> findByUserId(UUID userId, Sort sort);
}
