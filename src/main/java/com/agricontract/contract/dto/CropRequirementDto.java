package com.agricontract.contract.dto;

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
public class CropRequirementDto {
    private UUID id;
    private UUID buyerId;

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive in crates")
    private Double quantity;

    @NotBlank(message = "Quality grade is required")
    private String qualityGrade;

    @NotBlank(message = "Season is required")
    private String season;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String status;
}
