package com.agricontract.marketplace.service.serviceImpl;

import com.agricontract.common.exception.AppException;
import com.agricontract.marketplace.entity.MarketplaceListing;
import com.agricontract.marketplace.enums.ListingStatus;
import com.agricontract.marketplace.repository.MarketplaceListingRepository;
import com.agricontract.marketplace.service.MarketplaceListingService;
import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.service.AIPriceSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketplaceListingServiceImpl
        implements MarketplaceListingService {

    private final MarketplaceListingRepository listingRepository;
    private final AIPriceSnapshotService aiPriceSnapshotService;

    @Override
    public MarketplaceListing createListing(
            UUID farmerId,
            String crop,
            String state,
            String variety,
            Double quantity,
            String unit
    ) {

        // advisor pricing
        AIPriceSnapshot snapshot = aiPriceSnapshotService.forMarketplace(crop, state);

        MarketplaceListing listing = MarketplaceListing.builder()
                .farmerId(farmerId)
                .crop(crop)
                .state(state)
                .variety(variety)
                .quantity(quantity)
                .unit(unit)
                .basePrice(snapshot.getFairMinPrice())
                .build();

        return listingRepository.save(listing);
    }

    @Override
    public void cancelListing(UUID listingId, UUID farmerId) {

        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new AppException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (!listing.getFarmerId().equals(farmerId)) {
            throw new AppException(HttpStatus.FORBIDDEN,
                    "Only listing owner can cancel");
        }

        if (listing.getStatus() == ListingStatus.LOCKED) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel after bidding has started");
        }

        listing.setStatus(ListingStatus.CANCELLED);

        listingRepository.save(listing);
    }
}
