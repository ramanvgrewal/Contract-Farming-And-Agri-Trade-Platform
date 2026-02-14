package com.agricontract.marketplace.entity;

import com.agricontract.marketplace.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "marketplace_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Farmer who created listing
    @Column(nullable = false)
    private UUID farmerId;

    @Column(nullable = false)
    private String crop;

    @Column(nullable = false)
    private String state;

    private String variety;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String unit;

    // AI suggested starting price
    @Column(nullable = false)
    private Double basePrice;

    // Dynamic bidding state
    private Double currentHighestBid;

    private UUID highestBidderId;

    private Instant biddingStartTime;

    private Instant biddingEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        status = ListingStatus.OPEN;
        currentHighestBid = basePrice;
    }
}
