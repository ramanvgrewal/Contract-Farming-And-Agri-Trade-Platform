package com.agricontract.marketplace.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BidPlacedDTO {
    private Double currentHighestBidAmount;
    private UUID highestBidderId;
}
