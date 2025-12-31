package com.agricontract.marketplace.service;

import com.agricontract.auth.entity.User;
import com.agricontract.common.exception.AppException;
import com.agricontract.marketplace.dto.DealDto;
import com.agricontract.marketplace.dto.OfferDto;
import com.agricontract.marketplace.dto.ProduceListingDto;
import com.agricontract.marketplace.entity.Deal;
import com.agricontract.marketplace.entity.Offer;
import com.agricontract.marketplace.entity.ProduceListing;
import com.agricontract.marketplace.mapper.MarketplaceMapper;
import com.agricontract.marketplace.repository.DealRepository;
import com.agricontract.marketplace.repository.OfferRepository;
import com.agricontract.marketplace.repository.ProduceListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final ProduceListingRepository listingRepository;
    private final OfferRepository offerRepository;
    private final DealRepository dealRepository;
    private final MarketplaceMapper marketplaceMapper;

    @Transactional
    public ProduceListingDto createListing(ProduceListingDto dto, User farmer) {
        ProduceListing listing = marketplaceMapper.toListingEntity(dto);
        listing.setFarmer(farmer);
        listing.setStatus(ProduceListing.ListingStatus.AVAILABLE);
        return marketplaceMapper.toListingDto(listingRepository.save(listing));
    }

    public List<ProduceListingDto> getAllListings() {
        return listingRepository.findAll().stream()
                .map(marketplaceMapper::toListingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OfferDto makeOffer(OfferDto dto, User buyer) {
        ProduceListing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (listing.getStatus() != ProduceListing.ListingStatus.AVAILABLE) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Listing is no longer available");
        }

        Offer offer = marketplaceMapper.toOfferEntity(dto);
        offer.setListing(listing);
        offer.setBuyer(buyer);
        offer.setStatus(Offer.OfferStatus.PENDING);

        return marketplaceMapper.toOfferDto(offerRepository.save(offer));
    }

    @Transactional
    public DealDto acceptOffer(UUID offerId, User farmer) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Offer not found"));

        if (!offer.getListing().getFarmer().getId().equals(farmer.getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only the farmer who listed can accept offers");
        }

        offer.setStatus(Offer.OfferStatus.ACCEPTED);
        ProduceListing listing = offer.getListing();
        listing.setStatus(ProduceListing.ListingStatus.SOLD);

        Deal deal = Deal.builder()
                .listing(listing)
                .buyer(offer.getBuyer())
                .farmer(farmer)
                .finalPrice(offer.getOfferedPrice())
                .quantity(listing.getQuantity())
                .status(Deal.DealStatus.COMPLETED)
                .completedAt(LocalDateTime.now())
                .build();

        offerRepository.save(offer);
        listingRepository.save(listing);
        return marketplaceMapper.toDealDto(dealRepository.save(deal));
    }
}
