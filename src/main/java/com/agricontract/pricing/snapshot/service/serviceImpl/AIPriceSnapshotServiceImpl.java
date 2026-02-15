package com.agricontract.pricing.snapshot.service.serviceImpl;

import com.agricontract.pricing.intelligence.dto.ArrivalPressureResult;
import com.agricontract.pricing.intelligence.dto.PriceDynamicsResult;
import com.agricontract.pricing.intelligence.dto.SeasonalPriceBaseline;
import com.agricontract.pricing.intelligence.service.ArrivalPressureService;
import com.agricontract.pricing.intelligence.service.PriceDynamicsService;
import com.agricontract.pricing.intelligence.service.SeasonalPriceService;
import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.service.AIPriceSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIPriceSnapshotServiceImpl implements AIPriceSnapshotService {

    private final ArrivalPressureService arrivalPressureService;
    private final SeasonalPriceService seasonalPriceService;
    private final PriceDynamicsService priceDynamicsService;

    // ================= MARKETPLACE =================

    @Override
    public AIPriceSnapshot forMarketplace(String crop, String state) {

        ArrivalPressureResult pressure =
                arrivalPressureService.calculate(crop, state);

        PriceDynamicsResult dynamics =
                priceDynamicsService.calculate(crop, state);

        SeasonalPriceBaseline baseline =
                seasonalPriceService.calculate(crop, state,
                        java.time.LocalDate.now().getMonthValue());

        List<String> reasons = new ArrayList<>();

        double min = baseline.getLowerBand();
        double max = baseline.getUpperBand();

        if (pressure.getLevel() == ArrivalPressureResult.PressureLevel.HIGH) {
            max *= 0.95;
            reasons.add("HIGH_ARRIVALS");
        }

        if (dynamics.getTrend() == PriceDynamicsResult.Trend.DOWN) {
            max *= 0.97;
            reasons.add("PRICE_TREND_DOWN");
        }

        if (dynamics.getVolatilityLevel() == PriceDynamicsResult.VolatilityLevel.HIGH) {
            reasons.add("HIGH_VOLATILITY");
        }

        return AIPriceSnapshot.builder()
                .crop(crop)
                .state(state)
                .mode(AIPriceSnapshot.SnapshotMode.MARKETPLACE)
                .fairMinPrice(round(min))
                .fairMaxPrice(round(max))
                .unit("INR_PER_QUINTAL")
                .confidence(confidenceFromVolatility(dynamics))
                .reasonCodes(reasons)
                .build();
    }

    // ================= CONTRACT =================

    @Override
    public AIPriceSnapshot forContract(
            String crop,
            String state,
            int startMonth,
            int endMonth
    ) {

        List<SeasonalPriceBaseline> baselines = new ArrayList<>();

        for (int m : resolveMonths(startMonth, endMonth)) {
            SeasonalPriceBaseline b =
                    seasonalPriceService.calculate(crop, state, m);
            if (b.getAveragePrice() > 0) {
                baselines.add(b);
            }
        }

        double avg =
                baselines.stream()
                        .mapToDouble(SeasonalPriceBaseline::getAveragePrice)
                        .average()
                        .orElse(0);

        double upper =
                baselines.stream()
                        .mapToDouble(SeasonalPriceBaseline::getUpperBand)
                        .average()
                        .orElse(0);

        double std =
                baselines.stream()
                        .mapToDouble(b -> b.getUpperBand() - b.getLowerBand())
                        .average()
                        .orElse(0) / 2;

        double volatilityRatio = avg == 0 ? 0 : std / avg;

        double premiumPercent = determineRiskPremium(volatilityRatio);

        double contractMin = avg * (1 + premiumPercent);
        double contractMax = upper * (1 + premiumPercent);

        List<String> reasons = new ArrayList<>();
        reasons.add("SEASONAL_AVERAGE_USED");
        reasons.add("FARMER_RISK_PREMIUM_APPLIED");

        if (premiumPercent >= 0.15) {
            reasons.add("HIGH_SEASONAL_VARIANCE");
        }

        return AIPriceSnapshot.builder()
                .crop(crop)
                .state(state)
                .mode(AIPriceSnapshot.SnapshotMode.CONTRACT)
                .fairMinPrice(round(contractMin))
                .fairMaxPrice(round(contractMax))
                .unit("INR_PER_QUINTAL")
                .harvestStartMonth(startMonth)
                .harvestEndMonth(endMonth)
                .confidence(confidenceFromVolatility(volatilityRatio))
                .reasonCodes(reasons)
                .build();
    }

    // ================= HELPERS =================

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private AIPriceSnapshot.ConfidenceLevel confidenceFromVolatility(
            PriceDynamicsResult dynamics) {

        return switch (dynamics.getVolatilityLevel()) {
            case LOW -> AIPriceSnapshot.ConfidenceLevel.HIGH;
            case MEDIUM -> AIPriceSnapshot.ConfidenceLevel.MEDIUM;
            case HIGH -> AIPriceSnapshot.ConfidenceLevel.LOW;
        };
    }

    private double determineRiskPremium(double volatilityRatio) {
        if (volatilityRatio > 0.25) return 0.15;
        if (volatilityRatio > 0.10) return 0.10;
        return 0.05;
    }

    private AIPriceSnapshot.ConfidenceLevel confidenceFromVolatility(double v) {
        if (v > 0.25) return AIPriceSnapshot.ConfidenceLevel.LOW;
        if (v > 0.10) return AIPriceSnapshot.ConfidenceLevel.MEDIUM;
        return AIPriceSnapshot.ConfidenceLevel.HIGH;
    }

    private List<Integer> resolveMonths(int startMonth, int endMonth) {

        // 🔒 Validate input
        if (startMonth < 1 || startMonth > 12 ||
                endMonth < 1 || endMonth > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        List<Integer> months = new ArrayList<>();

        if (startMonth <= endMonth) {
            // Normal case: e.g. 3 → 6
            for (int m = startMonth; m <= endMonth; m++) {
                months.add(m);
            }
        } else {
            // Wrap-around case: e.g. 12 → 2
            for (int m = startMonth; m <= 12; m++) {
                months.add(m);
            }
            for (int m = 1; m <= endMonth; m++) {
                months.add(m);
            }
        }

        return months;
    }

}
