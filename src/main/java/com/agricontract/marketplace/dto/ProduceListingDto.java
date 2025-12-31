package com.agricontract.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduceListingDto {
    private UUID id;
    private UUID farmerId;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotBlank(message = "Quality grade is required")
    private String qualityGrade;

    @NotNull(message = "Expected price is required")
    @Positive(message = "Expected price must be positive")
    private BigDecimal expectedPrice;

    private String status;
}
