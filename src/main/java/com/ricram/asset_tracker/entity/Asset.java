package com.ricram.asset_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "symbol", nullable = false, unique = true, length = 50)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AssetType type;

    @Column(name = "quote_currency", nullable = false, length = 3)
    private String quoteCurrency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
    }

}
