package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.util.RequestPeriod;

import java.util.List;

public interface CurrencyRateService {

    List<Rate> getCurrencyRates(String baseCurrency, List<String> sourceCurrencies, RequestPeriod requestPeriod);
    List<Rate> fetchRates(String baseCode, List<String> codes);

}
