package com.ricram.asset_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString(exclude = "asset")
@Table(name = "asset_prices")
public class AssetPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "price", nullable = false, precision = 18, scale = 8)
    private BigDecimal price;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;
}
