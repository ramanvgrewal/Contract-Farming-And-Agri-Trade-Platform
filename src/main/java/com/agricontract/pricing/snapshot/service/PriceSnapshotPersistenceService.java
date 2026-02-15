package com.agricontract.pricing.snapshot.service;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;

import java.util.Optional;
import java.util.UUID;

public interface PriceSnapshotPersistenceService {

    PriceSnapshotEntity save(AIPriceSnapshot snapshot);

    Optional<PriceSnapshotEntity> findById(UUID id);
}
