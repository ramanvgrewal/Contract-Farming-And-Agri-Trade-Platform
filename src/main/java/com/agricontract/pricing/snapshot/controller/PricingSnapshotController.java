package com.agricontract.pricing.snapshot.controller;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.service.AIPriceSnapshotService;
import com.agricontract.pricing.snapshot.service.PriceSnapshotPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingSnapshotController {

    private final AIPriceSnapshotService aiPriceSnapshotService;
    private final PriceSnapshotPersistenceService priceSnapshotPersistenceService;

    // ================= MARKETPLACE =================

    @GetMapping("/marketplace")
    public AIPriceSnapshot marketplacePrice(
            @RequestParam String crop,
            @RequestParam String state
    ) {
        return aiPriceSnapshotService.forMarketplace(crop, state);
    }

    // ================= CONTRACT =================

    @GetMapping("/contract")
    public AIPriceSnapshot contractPrice(
            @RequestParam String crop,
            @RequestParam String state,
            @RequestParam int harvestStartMonth,
            @RequestParam int harvestEndMonth
    ) {

        // Calculate snapshot (business logic)
        return aiPriceSnapshotService.forContract(
                crop,
                state,
                harvestStartMonth,
                harvestEndMonth
        );
    }
}
