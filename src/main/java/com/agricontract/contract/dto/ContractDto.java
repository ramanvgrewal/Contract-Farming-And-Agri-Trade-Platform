package com.agricontract.contract.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractDto {
    private UUID id;
    @NotNull(message = "Requirement ID is required")
    private UUID requirementId;
    private UUID buyerId;
    private UUID farmerId;

    @NotNull(message = "Agreed price is required")
    @Positive(message = "Agreed price must be positive")
    private BigDecimal agreedPrice;

    @NotNull(message = "Agreed quantity is required")
    @Positive(message = "Agreed quantity must be positive")
    private Double agreedQuantity;

    private Date startDate;
    private Date endDate;
    private String status;
}
