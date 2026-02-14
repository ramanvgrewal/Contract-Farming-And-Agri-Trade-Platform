package com.agricontract.marketplace.service;

import java.util.UUID;

public interface MarketplaceBidService {

    void placeBid(UUID listingId, UUID bidderId, Double bidAmount);
}
