package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;

import java.util.List;
import java.util.Set;

public interface CurrencyService {

    Set<String> getCurrencyCodes();
    List<CurrencyDto> getCurrencies();
    CurrencyDto createCurrency(String code, CurrencyType type);
    CurrencyDto updateCurrency(String code, CurrencyType type);
}
