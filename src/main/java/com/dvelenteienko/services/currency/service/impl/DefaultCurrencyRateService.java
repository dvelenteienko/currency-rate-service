package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;
//    private final CustomCacheResolver rateCacheResolver;

    @Override
    public List<CurrencyRateDto> getCurrencyRates(String baseCode, RequestPeriodDto requestPeriod) {
        List<CurrencyRate> currencyRates = currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(baseCode,
                requestPeriod.getFrom(), requestPeriod.getTo());
        log.info("Getting currency rates: {}", currencyRates.size());
        return CurrencyRateDto.toDto(currencyRates).stream().distinct().toList();
    }

    @Override
    public List<CurrencyRateDto> populateRate(String baseCode, Set<String> codes) {
        if (codes.isEmpty()) {
            log.warn("No currency codes present!");
            throw new NoSuchElementException("Currency codes is empty");
        }
        List<CurrencyRateDto> currencyRateDtos = currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes);
        currencyRateRepository.saveAllAndFlush(CurrencyRateDto.fromDto(currencyRateDtos));
        return currencyRateDtos;
    }
}
