package com.agricontract.pricing.snapshot.repository;

import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceSnapshotRepository
        extends JpaRepository<PriceSnapshotEntity, UUID> {
}
