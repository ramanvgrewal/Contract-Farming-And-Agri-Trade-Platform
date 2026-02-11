package com.agricontract.pricing.intelligence.service;

import com.agricontract.pricing.intelligence.dto.PriceDynamicsResult;

public interface PriceDynamicsService {

    PriceDynamicsResult calculate(String crop, String state);
}
