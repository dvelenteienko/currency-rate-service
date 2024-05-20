package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;

import java.util.List;
import java.util.Set;

public interface CurrencyRateService {

    List<CurrencyRateDto> getCurrencyRates(String baseCode, RequestPeriodDto requestPeriod, CurrencyType type);
    List<CurrencyRateDto> fetchRates(String baseCode, Set<String> codes);

}
