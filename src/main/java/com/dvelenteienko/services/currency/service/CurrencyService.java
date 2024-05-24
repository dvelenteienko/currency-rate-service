package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;

import java.util.List;

public interface CurrencyService {
    List<CurrencyDto> getCurrencies();

    CurrencyDto getCurrencyByCode(String code);

    CurrencyDto createCurrency(String code, CurrencyType type);

    void removeCurrency(Currency currency);
}
