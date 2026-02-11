package com.agricontract.pricing.intelligence.service;

import com.agricontract.pricing.intelligence.dto.ArrivalPressureResult;

public interface ArrivalPressureService {

    ArrivalPressureResult calculate(String crop, String state);
}
