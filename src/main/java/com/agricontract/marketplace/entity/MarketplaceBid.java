package com.agricontract.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "marketplace_bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceBid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID listingId;

    @Column(nullable = false)
    private UUID bidderId;

    @Column(nullable = false)
    private Double bidAmount;

    @Column(nullable = false, updatable = false)
    private Instant placedAt;

    @PrePersist
    protected void onCreate() {
        placedAt = Instant.now();
    }
}
