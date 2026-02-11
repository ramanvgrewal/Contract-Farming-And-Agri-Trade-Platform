package com.agricontract.pricing.snapshot.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AIPriceSnapshot {

    private String crop;
    private String state;

    private SnapshotMode mode;

    private double fairMinPrice;
    private double fairMaxPrice;
    private String unit; // INR_PER_QUINTAL

    private ConfidenceLevel confidence;

    private List<String> reasonCodes;

    public enum SnapshotMode {
        MARKETPLACE,
        CONTRACT
    }

    public enum ConfidenceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}
