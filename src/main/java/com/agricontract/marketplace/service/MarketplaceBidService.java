package com.agricontract.marketplace.service;

import com.agricontract.marketplace.dto.BidPlacedDTO;

import java.util.UUID;

public interface MarketplaceBidService {

    BidPlacedDTO placeBid(UUID listingId, UUID bidderId, Double bidAmount);
}
