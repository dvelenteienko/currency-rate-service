package com.dvelenteienko.services.currency.domain.dto;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "set")
public class CurrencyRateDto {

    private String base;
    private String source;
    private LocalDateTime date;
    private Double rate;

    public static List<Rate> from(List<CurrencyRateDto> currencyRateDtos, Currency currency) {
        return currencyRateDtos.stream()
                .map(cr -> Rate.builder()
                        .setSource(cr.getSource())
                        .setBaseCurrencyCode(currency)
                        .setDate(cr.getDate())
                        .setRate(cr.getRate())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<CurrencyRateDto> from(List<Rate> rates) {
        return rates.stream()
                .map(cr -> CurrencyRateDto.builder()
                        .setSource(cr.getSource())
                        .setBase(cr.getBaseCurrencyCode().getCode())
                        .setDate(cr.getDate())
                        .setRate(cr.getRate())
                        .build())
                .collect(Collectors.toList());
    }

}
