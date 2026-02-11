package com.agricontract.pricing.snapshot.service;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;

public interface AIPriceSnapshotService {

    AIPriceSnapshot forMarketplace(String crop, String state);

    AIPriceSnapshot forContract(
            String crop,
            String state,
            int harvestStartMonth,
            int harvestEndMonth
    );
}
