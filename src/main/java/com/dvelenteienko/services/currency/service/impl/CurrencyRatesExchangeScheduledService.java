package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyRatesExchangeScheduledService {

    private final CurrencyRateService currencyRateService;
    private final CurrencyService currencyService;

    @Scheduled(cron = "${service.scheduler.cron}")
    public void retrieveCurrencyExchangeRate() {
      log.info("Starting scheduler of retrieve currency rates");
      String baseCode = currencyService.getCurrencyCodes(CurrencyType.BASE).stream()
              .filter(StringUtils::isNotBlank)
              .findFirst()
              .orElse(null);
      if (StringUtils.isNotBlank(baseCode)) {
          Set<String> currencyCodes = currencyService.getCurrencyCodes(CurrencyType.SOURCE);
          log.info("Prepare parameters. Base currency code: {}; Source codes: {}", baseCode, currencyCodes.size());
          List<CurrencyRateDto> currencyRateDtos = currencyRateService.createCurrencyRates(baseCode, currencyCodes);
          log.info("Currency rates successfully retrieved. Count: {}", currencyRateDtos.size());
      }
    }
}
