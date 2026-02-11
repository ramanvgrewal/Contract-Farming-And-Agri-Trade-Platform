//package com.agricontract.pricing.intelligence.runner;
//
//import com.agricontract.pricing.intelligence.service.SeasonalPriceService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SeasonalPriceTestRunner implements CommandLineRunner {
//
//    private final SeasonalPriceService seasonalPriceService;
//
//    @Override
//    public void run(String... args) {
//
//        var result = seasonalPriceService.calculate(
//                "Tomato",
//                "Uttar Pradesh",
//                2
//        );
//
//        System.out.println("===== SEASONAL PRICE BASELINE TEST =====");
//        System.out.println(result);
//        System.out.println("=======================================");
//    }
//}
//
