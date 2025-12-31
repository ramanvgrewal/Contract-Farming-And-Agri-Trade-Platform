package com.agricontract.marketplace.dto;

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
public class OfferDto {
    private UUID id;
    @NotNull(message = "Listing ID is required")
    private UUID listingId;
    private UUID buyerId;

    @NotNull(message = "Offered price is required")
    @Positive(message = "Offered price must be positive")
    private BigDecimal offeredPrice;

    private Integer roundNumber;
    private String status;
}
