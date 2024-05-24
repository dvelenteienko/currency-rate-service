package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;

import java.util.List;

public interface CurrencyExchangeDataService {

    List<CurrencyRateDto> getExchangeCurrencyRate(String baseCurrency, List<String> codes);

}
