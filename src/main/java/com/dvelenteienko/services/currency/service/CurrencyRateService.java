package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.util.RequestPeriod;

import java.util.List;

public interface CurrencyRateService {

    List<Rate> getCurrencyRatesByBase(Currency currency, RequestPeriod requestPeriod);
    List<Rate> getCurrencyRatesBySource(Currency currency, RequestPeriod requestPeriod);
    List<Rate> getCurrencyRates(Currency baseCurrency, List<Currency> sourceCurrencies, RequestPeriod requestPeriod);
    List<Rate> getCurrencyRatesByPeriod(RequestPeriod requestPeriod);

    List<Rate> fetchRates(Currency currency, List<String> codes);

//    boolean isCurrencyExistBySource(String source);

    void removeRatesBySource(String code);

}
