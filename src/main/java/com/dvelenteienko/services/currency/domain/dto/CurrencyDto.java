package com.dvelenteienko.services.currency.domain.dto;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder(setterPrefix = "set")
public class CurrencyDto {

    @JsonIgnore
    private UUID id;

    private String code;

    @JsonIgnore
    private CurrencyType type;

    public static CurrencyDto from(Currency currency) {
        return CurrencyDto.builder()
                .setId(currency.getId())
                .setCode(currency.getCode())
                .setType(currency.getType())
                .build();
    }

    public static List<CurrencyDto> from(List<Currency> currencies) {
        return currencies.stream()
                .map(c -> CurrencyDto.builder()
                        .setId(c.getId())
                        .setCode(c.getCode())
                        .setType(c.getType())
                        .build())
                .collect(Collectors.toList());
    }
    public static Currency from(CurrencyDto currencyDto) {
        return Currency.builder()
                .setId(currencyDto.getId())
                .setCode(currencyDto.getCode())
                .setType(currencyDto.getType())
                .build();
    }
}
