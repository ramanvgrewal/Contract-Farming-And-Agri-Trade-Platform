package com.agricontract.pricing.snapshot.runner;

import com.agricontract.pricing.snapshot.service.AIPriceSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIPriceTestRunner implements CommandLineRunner {

    private final AIPriceSnapshotService aiPriceSnapshotService;

    @Override
    public void run(String... args) {
        var marketplace =
                aiPriceSnapshotService.forMarketplace("Tomato", "Uttar Pradesh");

        var contract =
                aiPriceSnapshotService.forContract("Tomato", "Uttar Pradesh", 12, 2);

        System.out.println(marketplace);
        System.out.println(contract);
    }
}
