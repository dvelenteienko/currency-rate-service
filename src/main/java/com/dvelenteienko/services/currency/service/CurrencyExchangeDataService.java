package com.dvelenteienko.services.currency.service;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Rate;

import java.util.List;

public interface CurrencyExchangeDataService {

    List<CurrencyRateDTO> getExchangeCurrencyRate(String baseCurrency, List<String> codes);

}
