package com.agricontract.pricing.intelligence.service;

import com.agricontract.pricing.intelligence.dto.SeasonalPriceBaseline;

public interface SeasonalPriceService {

    SeasonalPriceBaseline calculate(String crop, String state, int month);
}
