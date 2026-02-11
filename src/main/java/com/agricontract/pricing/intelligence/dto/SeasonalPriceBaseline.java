package com.agricontract.pricing.intelligence.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeasonalPriceBaseline {

    private String crop;
    private String state;
    private int month;

    private double averagePrice;
    private double lowerBand;
    private double upperBand;
}
