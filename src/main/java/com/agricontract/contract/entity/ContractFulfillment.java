package com.agricontract.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contract_fulfillment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractFulfillment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Builder.Default
    @Column(name = "harvested_quantity")
    private Double harvestedQuantity = 0.0;

    @Builder.Default
    @Column(name = "delivered_quantity")
    private Double deliveredQuantity = 0.0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
