package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.util.RequestPeriod;

import java.util.List;

public interface CurrencyRateService {

    List<Rate> getRates(String baseCurrency, List<String> sourceCurrencies, RequestPeriod requestPeriod);
//    List<Rate> fetchAndMergeRates(String targetCode, List<String> currenciesToFetch, RequestPeriod requestPeriod);

    List<Rate> persisRates(String baseCode, List<String> codes, RequestPeriod requestPeriod);

    void fetchAndPersistRates(String baseCurrency, List<String> codes);
}
