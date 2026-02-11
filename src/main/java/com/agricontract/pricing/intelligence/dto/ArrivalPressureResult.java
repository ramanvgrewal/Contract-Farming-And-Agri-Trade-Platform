package com.agricontract.pricing.intelligence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArrivalPressureResult {

    private String crop;
    private String state;

    private double recentAvgArrivals;
    private double seasonalAvgArrivals;

    private double pressureRatio;
    private PressureLevel level;

    public enum PressureLevel {
        LOW,
        NORMAL,
        HIGH
    }
}
