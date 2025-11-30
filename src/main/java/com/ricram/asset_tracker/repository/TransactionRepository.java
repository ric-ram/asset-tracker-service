package com.ricram.asset_tracker.repository;

import com.ricram.asset_tracker.entity.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByPortfolioId(UUID portfolioId, Sort sort);

    List<Transaction> findByPortfolioIdAndAssetId(UUID portfolioId, UUID assetId, Sort sort);

    List<Transaction> findByPortfolioIdAndType(UUID portfolioId, Transaction type, Sort sort);
}
