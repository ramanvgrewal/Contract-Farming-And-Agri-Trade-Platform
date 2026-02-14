package com.agricontract.marketplace.service;

import com.agricontract.marketplace.entity.MarketplaceListing;

import java.util.UUID;

public interface MarketplaceListingService {

    MarketplaceListing createListing(
            UUID farmerId,
            String crop,
            String state,
            String variety,
            Double quantity,
            String unit
    );

    void cancelListing(UUID listingId, UUID farmerId);
}
