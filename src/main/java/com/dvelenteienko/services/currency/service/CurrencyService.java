package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;

import java.util.List;
import java.util.Set;

public interface CurrencyService {

    Set<String> getCurrencyCodes();
    List<Currency> getCurrencies();
    Currency createCurrency(String code, CurrencyType type);
    Currency updateCurrencyTypeByCode(String code, CurrencyType type);
}
