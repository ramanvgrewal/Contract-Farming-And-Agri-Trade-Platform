package com.agricontract.contract.entity;

import com.agricontract.contract.enums.ContractStatus;
import com.agricontract.contract.enums.PriceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Buyer user ID (from users table)
    // We Do not need to perform mapping, let's see in the future.
    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String state;

    private String cropVariety;

    @Column(nullable = false)
    private Double requiredQuantity;

    @Column(nullable = false)
    private Double filledQuantity = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType priceType;

    private Double offeredPriceMin;
    private Double offeredPriceMax;

    @Column(nullable = false)
    private UUID priceSnapshotId;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    @Column(nullable = false)
    private boolean locationRequired;

    private Double locationLat;
    private Double locationLng;
    private Double locationRadiusKm;

    @Column(nullable = false)
    private boolean clusterEnabled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    private Instant lockDeadline;

    @OneToMany(
            mappedBy = "contract",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FarmerCommitment> commitments = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = ContractStatus.CREATED;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
