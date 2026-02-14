package com.agricontract.marketplace.service.serviceImpl;

import com.agricontract.common.exception.AppException;
import com.agricontract.marketplace.entity.MarketplaceBid;
import com.agricontract.marketplace.entity.MarketplaceListing;
import com.agricontract.marketplace.enums.ListingStatus;
import com.agricontract.marketplace.repository.MarketplaceBidRepository;
import com.agricontract.marketplace.repository.MarketplaceListingRepository;
import com.agricontract.marketplace.service.MarketplaceBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketplaceBidServiceImpl implements MarketplaceBidService {

    private final MarketplaceListingRepository listingRepository;
    private final MarketplaceBidRepository bidRepository;

    @Override
    @Transactional
    public void placeBid(UUID listingId, UUID bidderId, Double bidAmount) {

        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new AppException(HttpStatus.NOT_FOUND, "Listing not found"));

        // Rule 1: Listing must not be closed or canceled
        if (listing.getStatus() == ListingStatus.CLOSED ||
                listing.getStatus() == ListingStatus.CANCELLED) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Bidding is not allowed on this listing");
        }

        // Rule 2: Farmer cannot bid on own listing
        if (listing.getFarmerId().equals(bidderId)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Farmer cannot bid on own listing");
        }

        // Rule 3: Bid must be higher than the current highest
        if (bidAmount <= listing.getCurrentHighestBid()) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Bid must be higher than current highest bid");
        }

        Instant now = Instant.now();

        // Rule 4: If LOCKED, ensure timer not expired
        if (listing.getStatus() == ListingStatus.LOCKED) {
            if (now.isAfter(listing.getBiddingEndTime())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "Bidding window has closed");
            }
        }

        // FIRST BID LOGIC
        if (listing.getStatus() == ListingStatus.OPEN) {

            listing.setStatus(ListingStatus.LOCKED);
            listing.setBiddingStartTime(now);
            listing.setBiddingEndTime(now.plusSeconds(24 * 60 * 60));
        }

        // Save bid
        MarketplaceBid bid = MarketplaceBid.builder()
                .listingId(listingId)
                .bidderId(bidderId)
                .bidAmount(bidAmount)
                .build();

        bidRepository.save(bid);

        // Update listing
        listing.setCurrentHighestBid(bidAmount);
        listing.setHighestBidderId(bidderId);

        listingRepository.save(listing);
    }
}
