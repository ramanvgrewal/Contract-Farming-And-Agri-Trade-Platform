package com.agricontract.pricing.snapshot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "price_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String crop;
    private String state;

    @Enumerated(EnumType.STRING)
    private SnapshotMode mode;

    private double fairMinPrice;
    private double fairMaxPrice;

    private String unit;

    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidence;

    @ElementCollection
    @CollectionTable(
            name = "price_snapshot_reasons",
            joinColumns = @JoinColumn(name = "snapshot_id")
    )
    @Column(name = "reason_code")
    private List<String> reasonCodes;

    private LocalDateTime createdAt;

    // Optional future links
    private UUID contractId;
    private UUID marketplaceListingId;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum SnapshotMode {
        MARKETPLACE,
        CONTRACT
    }

    public enum ConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}
