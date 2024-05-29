package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;

import java.util.List;

public interface CurrencyService {
    List<Currency> getCurrencies();

//    Currency getCurrencyByCode(String code);
//    List<Currency> getCurrencyByCodes(List<String> currencyCodes);

    Currency createCurrency(String code);

    void removeCurrency(Currency currency);
}
