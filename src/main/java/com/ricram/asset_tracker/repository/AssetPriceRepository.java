package com.ricram.asset_tracker.repository;

import com.ricram.asset_tracker.entity.AssetPrice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetPriceRepository extends JpaRepository<AssetPrice, UUID> {
    Optional<AssetPrice> findFirstByAssetId(UUID assetId, Sort sort);

    List<AssetPrice> findByAssetId(UUID assetId, Sort sort);

    List<AssetPrice> findByAssetIdAndFetchedAtBetween(UUID assetId, Instant start, Instant end, Sort sort);
}
