package com.agricontract.pricing.intelligence.service.serviceImpl;

import com.agricontract.pricing.intelligence.dto.ArrivalPressureResult;
import com.agricontract.pricing.intelligence.dto.ArrivalPressureResult.PressureLevel;
import com.agricontract.pricing.intelligence.service.ArrivalPressureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArrivalPressureServiceImpl implements ArrivalPressureService {

    private final MongoTemplate mongoTemplate;

    @Override
    public ArrivalPressureResult calculate(String crop, String state) {

        LocalDate today = LocalDate.now();
        LocalDate last7Days = today.minusDays(7);

        double recentAvg = avgArrivals(crop, state, last7Days, today);
        double seasonalAvg = avgSeasonalArrivals(crop, state, today.getMonthValue());

        double ratio = seasonalAvg == 0 ? 1.0 : recentAvg / seasonalAvg;

        return ArrivalPressureResult.builder()
                .crop(crop)
                .state(state)
                .recentAvgArrivals(recentAvg)
                .seasonalAvgArrivals(seasonalAvg)
                .pressureRatio(ratio)
                .level(classify(ratio))
                .build();
    }

    private double avgArrivals(String crop, String state, LocalDate from, LocalDate to) {

        MatchOperation match = Aggregation.match(
                Criteria.where("crop").is(crop)
                        .and("state").is(state)
                        .and("date").gte(from).lte(to)
                        .and("arrival.quantity").ne(null)
        );

        GroupOperation group = Aggregation.group().avg("arrival.quantity").as("avg");

        Aggregation agg = Aggregation.newAggregation(match, group);

        Map result = mongoTemplate.aggregate(agg, "mandi_raw_data", Map.class)
                .getUniqueMappedResult();

        return result == null ? 0.0 : (double) result.get("avg");
    }

    private double avgSeasonalArrivals(String crop, String state, int month) {

        MatchOperation match = Aggregation.match(
                Criteria.where("crop").is(crop)
                        .and("state").is(state)
                        .and("month").is(month)
                        .and("arrival.quantity").ne(null)
        );

        GroupOperation group = Aggregation.group().avg("arrival.quantity").as("avg");

        Aggregation agg = Aggregation.newAggregation(match, group);

        Map result = mongoTemplate.aggregate(agg, "mandi_raw_data", Map.class)
                .getUniqueMappedResult();

        return result == null ? 0.0 : (double) result.get("avg");
    }

    private PressureLevel classify(double ratio) {
        if (ratio > 1.3) return PressureLevel.HIGH;
        if (ratio < 0.9) return PressureLevel.LOW;
        return PressureLevel.NORMAL;
    }
}
