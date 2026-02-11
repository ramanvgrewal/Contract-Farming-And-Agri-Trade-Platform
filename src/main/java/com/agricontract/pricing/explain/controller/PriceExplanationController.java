package com.agricontract.pricing.explain.controller;

import com.agricontract.pricing.explain.service.PriceExplanationService;
import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.service.AIPriceSnapshotService;
import com.agricontract.pricing.snapshot.service.PriceSnapshotPersistenceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pricing")
public class PriceExplanationController {

    private final PriceExplanationService priceExplanationService;
    private final AIPriceSnapshotService aiPriceSnapshotService;
    private final PriceSnapshotPersistenceService priceSnapshotPersistenceService;

    @GetMapping("/contract/explain")
    public Map<String, Object> contractPriceWithExplanation(
            @RequestParam String crop,
            @RequestParam String state,
            @RequestParam int harvestStartMonth,
            @RequestParam int harvestEndMonth
    ) {
        AIPriceSnapshot snapshot =
                aiPriceSnapshotService.forContract(
                        crop, state, harvestStartMonth, harvestEndMonth
                );

        priceSnapshotPersistenceService.save(snapshot);

        String explanation =
                priceExplanationService.explain(snapshot);

        return Map.of(
                "snapshot", snapshot,
                "explanation", explanation
        );
    }
}
