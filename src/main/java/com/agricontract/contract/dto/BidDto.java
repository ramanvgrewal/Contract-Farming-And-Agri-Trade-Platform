package com.agricontract.contract.dto;

import com.agricontract.contract.entity.Bid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class BidDto {
    private UUID id;
    private UUID contractId;
    private UUID bidderId;
    private Integer roundNumber;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String riskLevel;
    private String aiExplanation;
    private LocalDateTime createdAt;
}
