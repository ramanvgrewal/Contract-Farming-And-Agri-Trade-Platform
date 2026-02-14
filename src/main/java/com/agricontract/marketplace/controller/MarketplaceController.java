package com.agricontract.marketplace.controller;

import com.agricontract.marketplace.entity.MarketplaceBid;
import com.agricontract.marketplace.entity.MarketplaceListing;
import com.agricontract.marketplace.enums.ListingStatus;
import com.agricontract.marketplace.repository.MarketplaceBidRepository;
import com.agricontract.marketplace.repository.MarketplaceListingRepository;
import com.agricontract.marketplace.service.MarketplaceBidService;
import com.agricontract.marketplace.service.MarketplaceListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceListingService listingService;
    private final MarketplaceBidService bidService;
    private final MarketplaceListingRepository listingRepository;
    private final MarketplaceBidRepository bidRepository;

    // ================= CREATE LISTING =================

    @PostMapping("/listings")
    public MarketplaceListing createListing(
            @RequestParam UUID farmerId,
            @RequestParam String crop,
            @RequestParam String state,
            @RequestParam(required = false) String variety,
            @RequestParam Double quantity,
            @RequestParam String unit
    ) {
        return listingService.createListing(
                farmerId,
                crop,
                state,
                variety,
                quantity,
                unit
        );
    }

    // ================= CANCEL LISTING =================

    @PutMapping("/listings/{listingId}/cancel")
    public void cancelListing(
            @PathVariable UUID listingId,
            @RequestParam UUID farmerId
    ) {
        listingService.cancelListing(listingId, farmerId);
    }

    // ================= PLACE BID =================

    @PostMapping("/listings/{listingId}/bids")
    public void placeBid(
            @PathVariable UUID listingId,
            @RequestParam UUID bidderId,
            @RequestParam Double bidAmount
    ) {
        bidService.placeBid(listingId, bidderId, bidAmount);
    }

    // ================= GET ACTIVE LISTINGS =================

    @GetMapping("/listings/active")
    public List<MarketplaceListing> getActiveListings() {
        return listingRepository.findByStatus(ListingStatus.OPEN);
    }

    // ================= GET LOCKED LISTINGS =================

    @GetMapping("/listings/locked")
    public List<MarketplaceListing> getLockedListings() {
        return listingRepository.findByStatus(ListingStatus.LOCKED);
    }

    // ================= GET LISTING DETAILS =================

    @GetMapping("/listings/{listingId}")
    public MarketplaceListing getListing(@PathVariable UUID listingId) {
        return listingRepository.findById(listingId).orElseThrow();
    }

    // ================= GET BID HISTORY =================

    @GetMapping("/listings/{listingId}/bids")
    public List<MarketplaceBid> getBids(@PathVariable UUID listingId) {
        return bidRepository.findByListingIdOrderByBidAmountDesc(listingId);
    }
}
