package com.agricontract.pricing.intelligence.service.serviceImpl;

import com.agricontract.pricing.intelligence.dto.SeasonalPriceBaseline;
import com.agricontract.pricing.intelligence.service.SeasonalPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeasonalPriceServiceImpl implements SeasonalPriceService {

    private final MongoTemplate mongoTemplate;

    @Override
    public SeasonalPriceBaseline calculate(String crop, String state, int month) {

        MatchOperation match = Aggregation.match(
                Criteria.where("crop").is(crop)
                        .and("state").is(state)
                        .and("month").is(month)
                        .and("price.modal").ne(null)
        );

        GroupOperation group = Aggregation.group()
                .avg("price.modal").as("avg")
                .stdDevPop("price.modal").as("std");

        Aggregation agg = Aggregation.newAggregation(match, group);

        Map result = mongoTemplate.aggregate(agg, "mandi_raw_data", Map.class)
                .getUniqueMappedResult();

        double avg = result == null ? 0.0 : (double) result.get("avg");
        double std = result == null ? 0.0 : (double) result.get("std");

        return SeasonalPriceBaseline.builder()
                .crop(crop)
                .state(state)
                .month(month)
                .averagePrice(avg)
                .lowerBand(Math.max(0, avg - std))
                .upperBand(avg + std)
                .build();
    }
}
