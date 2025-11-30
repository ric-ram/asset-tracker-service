package com.ricram.asset_tracker.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ricram.asset_tracker.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    Optional<Asset> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);

    List<Asset> findBySymbolIn(Collection<String> symbols);
}
