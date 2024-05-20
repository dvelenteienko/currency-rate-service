package com.dvelenteienko.services.currency.domain.dto;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "set")
public class CurrencyRateDto {

    private String source;
    private String base;
    private LocalDateTime date;
    private Double rate;

    public static List<CurrencyRate> fromDto(List<CurrencyRateDto> currencyRateDtos) {
        return currencyRateDtos.stream()
                .map(cr -> CurrencyRate.builder()
                        .rate(cr.getRate())
                        .date(cr.getDate())
                        .source(cr.getSource())
                        .baseCurrencyCode(Currency.builder()
                                .code(cr.getBase())
                                .type(CurrencyType.BASE)
                                .id(UUID.randomUUID()).build())
                        .build())
                .toList();
    }

    public static List<CurrencyRateDto> toDto(List<CurrencyRate> currencyRates) {
        return currencyRates.stream()
                .map(cr -> CurrencyRateDto.builder()
                        .setSource(cr.getSource())
                        .setBase(cr.getBaseCurrencyCode().getCode())
                        .setDate(cr.getDate())
                        .setRate(cr.getRate())
                        .build())
                .collect(Collectors.toList());
    }

}
