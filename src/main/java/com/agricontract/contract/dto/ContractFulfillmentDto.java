package com.agricontract.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractFulfillmentDto {
    private UUID id;
    private UUID contractId;
    private Double harvestedQuantity;
    private Double deliveredQuantity;
    private LocalDateTime lastUpdated;
}
