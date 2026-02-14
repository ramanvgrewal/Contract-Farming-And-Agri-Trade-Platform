package com.agricontract.marketplace.scheduler;

import com.agricontract.marketplace.entity.MarketplaceListing;
import com.agricontract.marketplace.enums.ListingStatus;
import com.agricontract.marketplace.repository.MarketplaceListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketplaceClosingScheduler {

    private final MarketplaceListingRepository listingRepository;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    @Transactional
    public void closeExpiredListings() {

        Instant now = Instant.now();

        List<MarketplaceListing> expiredListings =
                listingRepository.findByStatusAndBiddingEndTimeBefore(
                        ListingStatus.LOCKED,
                        now
                );

        for (MarketplaceListing listing : expiredListings) {

            listing.setStatus(ListingStatus.CLOSED);

            log.info("Listing closed: {} | Winner: {} | Final Price: {}",
                    listing.getId(),
                    listing.getHighestBidderId(),
                    listing.getCurrentHighestBid());

            // create contract in future if needed
        }

        listingRepository.saveAll(expiredListings);
    }
}
