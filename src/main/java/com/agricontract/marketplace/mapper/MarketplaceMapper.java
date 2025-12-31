package com.agricontract.marketplace.mapper;

import com.agricontract.marketplace.dto.DealDto;
import com.agricontract.marketplace.dto.OfferDto;
import com.agricontract.marketplace.dto.ProduceListingDto;
import com.agricontract.marketplace.entity.Deal;
import com.agricontract.marketplace.entity.Offer;
import com.agricontract.marketplace.entity.ProduceListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketplaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProduceListing toListingEntity(ProduceListingDto dto);

    @Mapping(target = "farmerId", source = "farmer.id")
    ProduceListingDto toListingDto(ProduceListing entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "listing", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Offer toOfferEntity(OfferDto dto);

    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "buyerId", source = "buyer.id")
    OfferDto toOfferDto(Offer entity);

    @Mapping(target = "listingId", source = "listing.id")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "farmerId", source = "farmer.id")
    DealDto toDealDto(Deal entity);
}
