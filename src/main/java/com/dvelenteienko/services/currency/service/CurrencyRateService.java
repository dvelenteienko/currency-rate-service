package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;

import java.util.List;

public interface CurrencyRateService {

    List<CurrencyRateDto> getCurrencyRates(Currency currency, RequestPeriodDto requestPeriod, CurrencyType type);

    List<CurrencyRateDto> fetchRates(Currency currency, List<String> codes);

//    boolean isCurrencyExistBySource(String source);

    void removeRatesBySource(String code);

}
