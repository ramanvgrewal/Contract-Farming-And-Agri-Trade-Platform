//package com.agricontract.marketData.runner;
//
//import com.agricontract.marketData.service.CsvMandiIngestionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//
//@Component
//@RequiredArgsConstructor
//public class CsvIngestionRunner implements CommandLineRunner {
//
//    private final CsvMandiIngestionService ingestionService;
//
//    @Override
//    public void run(String... args) {
//
//        ingestionService.ingest(new File("C:/Users/raman/test/SavvyFarm/data/tomato_up.csv"));
//        ingestionService.ingest(new File("C:/Users/raman/test/SavvyFarm/data/onion_up.csv"));
//        ingestionService.ingest(new File("C:/Users/raman/test/SavvyFarm/data/potato_up.csv"));
//        ingestionService.ingest(new File("C:/Users/raman/test/SavvyFarm/data/wheat_up.csv"));
//    }
//}
