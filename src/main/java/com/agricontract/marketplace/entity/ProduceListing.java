package com.agricontract.marketplace.entity;

import com.agricontract.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "produce_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "quality_grade", nullable = false)
    private String qualityGrade;

    @Column(name = "expected_price", nullable = false)
    private BigDecimal expectedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ListingStatus.AVAILABLE;
    }

    public enum ListingStatus {
        AVAILABLE, SOLD, CANCELLED
    }
}
