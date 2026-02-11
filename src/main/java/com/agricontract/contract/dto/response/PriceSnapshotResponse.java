package com.agricontract.contract.dto.response;

import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PriceSnapshotResponse {

    private UUID snapshotId;

    private PriceSnapshotEntity.SnapshotMode mode;

    private Double fairMinPrice;
    private Double fairMaxPrice;
    private String unit;

    private PriceSnapshotEntity.ConfidenceLevel confidence;

    private List<String> reasonCodes;
}
