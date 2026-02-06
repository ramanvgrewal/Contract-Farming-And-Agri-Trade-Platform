package com.agricontract.contract.dto.request;

import com.agricontract.contract.enums.PriceType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateContractRequest {

    @NotBlank
    private String cropName;

    private String cropVariety;

    @NotNull
    @Positive
    private Double requiredQuantity;

    @NotNull
    private PriceType priceType;

    @Positive
    private Double offeredPriceMin;

    @Positive
    private Double offeredPriceMax;

    @NotNull
    private LocalDate contractStartDate;

    @NotNull
    private LocalDate contractEndDate;

    // Location (optional)
    private Boolean locationRequired = false;
    private Double locationLat;
    private Double locationLng;
    private Double locationRadiusKm;

    // Cluster
    private Boolean clusterEnabled = false;
}
