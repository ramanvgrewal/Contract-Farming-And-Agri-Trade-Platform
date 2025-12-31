package com.agricontract.contract.entity;

import com.agricontract.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private CropRequirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(name = "agreed_price", nullable = false)
    private BigDecimal agreedPrice;

    @Column(name = "agreed_quantity", nullable = false)
    private Double agreedQuantity;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL)
    private ContractFulfillment fulfillment;

    public enum ContractStatus {
        NEGOTIATING, ACCEPTED, REJECTED, EXPIRED, SIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
