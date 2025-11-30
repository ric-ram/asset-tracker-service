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
@ToString(exclude = {"asset", "portfolio"})
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 8)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, precision = 18, scale = 8)
    private BigDecimal price;

    @Column(name = "fee", nullable = false, precision = 18, scale = 8)
    private BigDecimal fee;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    private void onCreate() { this.createdAt = Instant.now(); }

}
