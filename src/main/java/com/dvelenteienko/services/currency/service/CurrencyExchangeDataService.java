package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;

import java.util.List;
import java.util.Set;

public interface CurrencyExchangeDataService {

    List<CurrencyRateDto> getExchangeCurrencyRate(String baseCurrency, Set<String> codes);

}
