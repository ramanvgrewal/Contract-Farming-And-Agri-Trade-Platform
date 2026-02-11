package com.agricontract.marketData.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "mandi_raw_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MandiRawData {

    @Id
    private String id;

    @Indexed
    private String crop;

    private String variety;
    private String grade;

    private String state;
    private String district;
    private String mandi;

    @Indexed
    private LocalDate date;

    private int year;
    private int month;

    private Price price;
    private Arrival arrival;

    private String source;
    private Instant ingestedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Price {
        private Double min;
        private Double max;
        private Double modal;
        private String unit;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Arrival {
        private Double quantity;
        private String unit;
    }
}
