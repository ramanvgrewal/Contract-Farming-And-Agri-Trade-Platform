package com.agricontract.pricing.snapshot.mapper;

import com.agricontract.contract.dto.response.PriceSnapshotResponse;
import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;
import org.springframework.stereotype.Component;

@Component
public class PriceSnapshotMapper {

    public PriceSnapshotEntity toEntity(AIPriceSnapshot dto) {
        return PriceSnapshotEntity.builder()
                .crop(dto.getCrop())
                .state(dto.getState())
                .mode(
                        PriceSnapshotEntity.SnapshotMode.valueOf(
                                dto.getMode().name()
                        )
                )
                .fairMinPrice(dto.getFairMinPrice())
                .fairMaxPrice(dto.getFairMaxPrice())
                .unit(dto.getUnit())
                .harvestStartMonth(dto.getHarvestStartMonth())
                .harvestEndMonth(dto.getHarvestEndMonth())
                .confidence(
                        PriceSnapshotEntity.ConfidenceLevel.valueOf(
                                dto.getConfidence().name()
                        )
                )
                .reasonCodes(dto.getReasonCodes())
                .build();
    }

    public PriceSnapshotResponse toResponse(PriceSnapshotEntity entity) {
        if (entity == null) return null;

        return PriceSnapshotResponse.builder()
                .snapshotId(entity.getId())
                .mode(entity.getMode())
                .fairMinPrice(entity.getFairMinPrice())
                .fairMaxPrice(entity.getFairMaxPrice())
                .unit(entity.getUnit())
                .confidence(entity.getConfidence())
                .reasonCodes(entity.getReasonCodes())
                .build();
    }

}
