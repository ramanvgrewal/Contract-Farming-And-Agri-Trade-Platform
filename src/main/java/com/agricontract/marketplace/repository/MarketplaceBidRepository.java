package com.agricontract.marketplace.repository;

import com.agricontract.marketplace.entity.MarketplaceBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarketplaceBidRepository
        extends JpaRepository<MarketplaceBid, UUID> {

    List<MarketplaceBid> findByListingId(UUID listingId);

    List<MarketplaceBid> findByListingIdOrderByBidAmountDesc(UUID listingId);
}
