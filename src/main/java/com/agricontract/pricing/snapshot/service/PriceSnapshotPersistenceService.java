package com.agricontract.pricing.snapshot.service;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;

public interface PriceSnapshotPersistenceService {

    PriceSnapshotEntity save(AIPriceSnapshot snapshot);
}
