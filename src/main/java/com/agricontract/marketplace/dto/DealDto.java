package com.agricontract.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealDto {
    private UUID id;
    private UUID listingId;
    private UUID buyerId;
    private UUID farmerId;
    private BigDecimal finalPrice;
    private Double quantity;
    private String status;
    private LocalDateTime completedAt;
}
