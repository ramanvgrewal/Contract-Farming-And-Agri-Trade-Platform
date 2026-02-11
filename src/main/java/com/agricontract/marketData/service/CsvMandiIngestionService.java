package com.agricontract.marketData.service;

import com.agricontract.marketData.document.MandiRawData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvMandiIngestionService {

    private final MongoTemplate mongoTemplate;

    private static final DateTimeFormatter AGMARKNET_DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ===================== PUBLIC API =====================

    public void ingest(File csvFile) {

        log.info("INGEST STARTED → {}", csvFile.getAbsolutePath());

        int successCount = 0;
        int skippedCount = 0;

        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8)
                )
        ) {

            // 1️⃣ Find the real header row
            String headerLine = null;
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("Commodity") && line.contains("Arrival Date")) {
                    headerLine = line;
                    break;
                }
            }

            if (headerLine == null) {
                throw new RuntimeException("Agmarknet header row not found");
            }

            // 2️⃣ Create CSV parser using detected header
            CSVParser parser = CSVFormat.DEFAULT
                    .withHeader(headerLine.split(","))
                    .withSkipHeaderRecord()
                    .withTrim()
                    .parse(br);

            log.info("CSV HEADERS → {}", parser.getHeaderMap().keySet());

            // 3️⃣ Process records
            for (CSVRecord record : parser) {
                try {
                    MandiRawData data = mapRecord(record);
                    upsert(data);
                    successCount++;
                } catch (Exception e) {
                    skippedCount++;
                    log.debug("Skipped row {} → {}", record.getRecordNumber(), e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV ingestion failed", e);
        }

        log.info("INGEST COMPLETED → {} | inserted/updated={} | skipped={}",
                csvFile.getName(), successCount, skippedCount);
    }

    // ===================== MAPPING =====================

    private MandiRawData mapRecord(CSVRecord r) {

        LocalDate date = parseDate(r.get("Arrival Date"));

        return MandiRawData.builder()
                .crop(r.get("Commodity").trim())
                .variety(blankToNull(r.get("Variety")))
                .grade(blankToNull(r.get("Grade")))
                .state(r.get("State").trim())        // ✅ FIXED
                .district(r.get("District").trim())
                .mandi(r.get("Market").trim())
                .date(date)
                .year(date.getYear())
                .month(date.getMonthValue())
                .price(
                        MandiRawData.Price.builder()
                                .min(parseDouble(r.get("Min Price")))
                                .max(parseDouble(r.get("Max Price")))
                                .modal(parseDouble(r.get("Modal Price")))
                                .unit(blankToNull(r.get("Price Unit")))
                                .build()
                )
                .arrival(
                        MandiRawData.Arrival.builder()
                                .quantity(parseDouble(r.get("Arrival Quantity")))
                                .unit(blankToNull(r.get("Arrival Unit")))
                                .build()
                )
                .source("agmarknet")
                .ingestedAt(Instant.now())
                .build();
    }


    // ===================== UPSERT =====================

    private void upsert(MandiRawData data) {

        Query query = new Query(
                Criteria.where("crop").is(data.getCrop())
                        .and("mandi").is(data.getMandi())
                        .and("date").is(data.getDate())
                        .and("variety").is(data.getVariety())
                        .and("grade").is(data.getGrade())
        );

        Update update = new Update()
                .set("state", data.getState())
                .set("district", data.getDistrict())
                .set("year", data.getYear())
                .set("month", data.getMonth())
                .set("price", data.getPrice())
                .set("arrival", data.getArrival())
                .set("source", data.getSource())
                .set("ingestedAt", data.getIngestedAt());

        mongoTemplate.upsert(query, update, MandiRawData.class);
    }

    // ===================== HELPERS =====================

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Arrival Date missing");
        }
        return LocalDate.parse(value.trim(), AGMARKNET_DATE);
    }

    private Double parseDouble(String v) {
        if (v == null) return null;

        v = v.trim();
        if (v.isEmpty() || v.equalsIgnoreCase("NR") || v.equals("-")) {
            return null;
        }
        return Double.parseDouble(v);
    }

    private String blankToNull(String v) {
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
