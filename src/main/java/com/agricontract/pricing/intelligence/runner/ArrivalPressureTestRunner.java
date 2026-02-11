//package com.agricontract.pricing.intelligence.runner;
//
//import com.agricontract.pricing.intelligence.service.ArrivalPressureService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class ArrivalPressureTestRunner implements CommandLineRunner {
//
//    private final ArrivalPressureService arrivalPressureService;
//
//    @Override
//    public void run(String... args) {
//
//        var result = arrivalPressureService.calculate(
//                "Tomato",
//                "Uttar Pradesh"
//        );
//
//        System.out.println("===== ARRIVAL PRESSURE TEST =====");
//        System.out.println(result);
//        System.out.println("=================================");
//    }
//}
