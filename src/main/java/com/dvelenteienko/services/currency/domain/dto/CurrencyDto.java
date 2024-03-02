package com.dvelenteienko.services.currency.domain.dto;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(setterPrefix = "set")
public class CurrencyDto {

    private String code;
    private CurrencyType type;

    public static CurrencyDto from(Currency currency) {
        return CurrencyDto.builder()
                .setCode(currency.getCode())
                .setType(currency.getType())
                .build();
    }

    public static List<CurrencyDto> fromAll(List<Currency> currencies) {
        return currencies.stream()
                .map(c -> new CurrencyDto(c.getCode(), c.getType()))
                .toList();
    }
}
