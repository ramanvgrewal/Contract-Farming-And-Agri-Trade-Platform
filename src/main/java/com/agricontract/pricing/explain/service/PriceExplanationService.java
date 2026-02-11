package com.agricontract.pricing.explain.service;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;

public interface PriceExplanationService {

    String explain(AIPriceSnapshot snapshot);
}
