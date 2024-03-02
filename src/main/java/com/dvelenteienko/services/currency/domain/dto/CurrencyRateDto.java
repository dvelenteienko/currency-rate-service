package com.dvelenteienko.services.currency.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(setterPrefix = "set")
public class CurrencyRateDto {

    private String source;
    private String target;
    private LocalDate date;
    private Double rate;


}
