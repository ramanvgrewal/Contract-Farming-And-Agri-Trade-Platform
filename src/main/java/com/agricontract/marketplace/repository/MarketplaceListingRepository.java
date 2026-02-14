package com.agricontract.marketplace.repository;

import com.agricontract.marketplace.entity.MarketplaceListing;
import com.agricontract.marketplace.enums.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MarketplaceListingRepository
        extends JpaRepository<MarketplaceListing, UUID> {

    List<MarketplaceListing> findByStatus(ListingStatus status);

    List<MarketplaceListing> findByStatusAndBiddingEndTimeBefore(
            ListingStatus status,
            Instant time
    );
}
