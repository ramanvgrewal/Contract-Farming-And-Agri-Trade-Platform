package com.agricontract.marketplace.controller;

import com.agricontract.auth.entity.User;
import com.agricontract.marketplace.dto.DealDto;
import com.agricontract.marketplace.dto.OfferDto;
import com.agricontract.marketplace.dto.ProduceListingDto;
import com.agricontract.marketplace.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping("/listings")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProduceListingDto> createListing(
            @Valid @RequestBody ProduceListingDto dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(marketplaceService.createListing(dto, user));
    }

    @GetMapping("/listings")
    public ResponseEntity<List<ProduceListingDto>> getAllListings() {
        return ResponseEntity.ok(marketplaceService.getAllListings());
    }

    @PostMapping("/offers")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OfferDto> makeOffer(
            @Valid @RequestBody OfferDto dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(marketplaceService.makeOffer(dto, user));
    }

    @PostMapping("/offers/{offerId}/accept")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<DealDto> acceptOffer(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(marketplaceService.acceptOffer(offerId, user));
    }
}
