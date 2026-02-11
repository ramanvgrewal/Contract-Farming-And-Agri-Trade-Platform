//package com.agricontract.pricing.intelligence.runner;
//
//import com.agricontract.pricing.intelligence.service.PriceDynamicsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class PriceDynamicsTestRunner implements CommandLineRunner {
//
//    private final PriceDynamicsService priceDynamicsService;
//
//    @Override
//    public void run(String... args) {
//
//        var result = priceDynamicsService.calculate(
//                "Tomato",
//                "Uttar Pradesh"
//        );
//
//        System.out.println("===== PRICE DYNAMICS TEST =====");
//        System.out.println(result);
//        System.out.println("================================");
//    }
//}
