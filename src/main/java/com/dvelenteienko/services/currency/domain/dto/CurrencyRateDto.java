package com.dvelenteienko.services.currency.domain.dto;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(setterPrefix = "set")
public class CurrencyRateDto {

    private String source;
    private String base;
    private LocalDate date;
    private Double rate;

    public static List<CurrencyRate> fromDto(List<CurrencyRateDto> currencyRateDtos) {
        return currencyRateDtos.stream()
                .map(cr -> new CurrencyRate(null, cr.getSource(), cr.getBase(), cr.getDate(), cr.getRate()))
                .toList();
    }

    public static List<CurrencyRateDto> toDto(List<CurrencyRate> currencyRates) {
        return currencyRates.stream()
                .map(cr -> new CurrencyRateDto(cr.getSource(), cr.getBase(), cr.getDate(), cr.getRate()))
                .toList();
    }

}
