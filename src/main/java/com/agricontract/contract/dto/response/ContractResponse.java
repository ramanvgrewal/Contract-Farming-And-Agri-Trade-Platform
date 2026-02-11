package com.agricontract.contract.dto.response;

import com.agricontract.contract.enums.ContractStatus;
import com.agricontract.contract.enums.PriceType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ContractResponse {

    private UUID id;

    private UUID buyerId;

    private String cropName;
    private String cropVariety;

    private Double requiredQuantity;
    private Double filledQuantity;

    private PriceType priceType;
    private Double offeredPriceMin;
    private Double offeredPriceMax;
    private PriceSnapshotResponse priceSnapshot;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private boolean locationRequired;
    private Double locationLat;
    private Double locationLng;
    private Double locationRadiusKm;

    private boolean clusterEnabled;

    private ContractStatus status;

    private Instant createdAt;

    private List<FarmerCommitmentResponse> commitments;
}
