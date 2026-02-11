package com.agricontract.pricing.intelligence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceDynamicsResult {

    private String crop;
    private String state;

    private double avg7Days;
    private double avg30Days;

    private double momentumRatio;
    private Trend trend;

    private double volatility;
    private VolatilityLevel volatilityLevel;

    public enum Trend {
        UP, DOWN, FLAT
    }

    public enum VolatilityLevel {
        LOW, MEDIUM, HIGH
    }
}
