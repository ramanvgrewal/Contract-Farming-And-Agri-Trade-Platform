package com.agricontract.pricing.intelligence.service.serviceImpl;

import com.agricontract.pricing.intelligence.dto.PriceDynamicsResult;
import com.agricontract.pricing.intelligence.dto.PriceDynamicsResult.Trend;
import com.agricontract.pricing.intelligence.dto.PriceDynamicsResult.VolatilityLevel;
import com.agricontract.pricing.intelligence.service.PriceDynamicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriceDynamicsServiceImpl implements PriceDynamicsService {

    private final MongoTemplate mongoTemplate;

    @Override
    public PriceDynamicsResult calculate(String crop, String state) {

        LocalDate today = LocalDate.now();

        double avg7 = avgPrice(crop, state, today.minusDays(7), today);
        double avg30 = avgPrice(crop, state, today.minusDays(30), today);

        double std30 = stdDevPrice(crop, state, today.minusDays(30), today);

        double momentumRatio = avg30 == 0 ? 1.0 : avg7 / avg30;
        double volatility = avg30 == 0 ? 0.0 : std30 / avg30;

        return PriceDynamicsResult.builder()
                .crop(crop)
                .state(state)
                .avg7Days(avg7)
                .avg30Days(avg30)
                .momentumRatio(momentumRatio)
                .trend(classifyTrend(momentumRatio))
                .volatility(volatility)
                .volatilityLevel(classifyVolatility(volatility))
                .build();
    }

    private double avgPrice(String crop, String state, LocalDate from, LocalDate to) {

        MatchOperation match = Aggregation.match(
                Criteria.where("crop").is(crop)
                        .and("state").is(state)
                        .and("date").gte(from).lte(to)
                        .and("price.modal").ne(null)
        );

        GroupOperation group = Aggregation.group().avg("price.modal").as("avg");

        Aggregation agg = Aggregation.newAggregation(match, group);

        Map result = mongoTemplate.aggregate(agg, "mandi_raw_data", Map.class)
                .getUniqueMappedResult();

        return result == null ? 0.0 : (double) result.get("avg");
    }

    private double stdDevPrice(String crop, String state, LocalDate from, LocalDate to) {

        MatchOperation match = Aggregation.match(
                Criteria.where("crop").is(crop)
                        .and("state").is(state)
                        .and("date").gte(from).lte(to)
                        .and("price.modal").ne(null)
        );

        GroupOperation group = Aggregation.group()
                .stdDevPop("price.modal").as("std");

        Aggregation agg = Aggregation.newAggregation(match, group);

        Map result = mongoTemplate.aggregate(agg, "mandi_raw_data", Map.class)
                .getUniqueMappedResult();

        return result == null ? 0.0 : (double) result.get("std");
    }

    private Trend classifyTrend(double ratio) {
        if (ratio > 1.05) return Trend.UP;
        if (ratio < 0.95) return Trend.DOWN;
        return Trend.FLAT;
    }

    private VolatilityLevel classifyVolatility(double v) {
        if (v > 0.25) return VolatilityLevel.HIGH;
        if (v > 0.10) return VolatilityLevel.MEDIUM;
        return VolatilityLevel.LOW;
    }
}
