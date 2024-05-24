package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;

    @Override
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME,
            key = "T(java.lang.String).format('%s-%s-%s-%s', #type, #currency.code, #requestPeriod.from, #requestPeriod.to)")
    public List<CurrencyRateDto> getCurrencyRates(Currency currency, RequestPeriodDto requestPeriod, CurrencyType type) {
        List<Rate> rates;
        if (CurrencyType.BASE == type) {
            rates = currencyRateRepository.findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc(currency,
                    requestPeriod.getFrom(),
                    requestPeriod.getTo());
        } else {
            rates = currencyRateRepository.findAllBySourceAndDateBetweenOrderByDateDesc(currency.getCode(),
                    requestPeriod.getFrom(),
                    requestPeriod.getTo());
        }
        log.info("Getting currency rates: {}", rates.size());
        return CurrencyRateDto.from(rates).stream().distinct().toList();
    }

    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public List<CurrencyRateDto> fetchRates(Currency currency, List<String> codes) {
        if (codes.isEmpty() || currency == null) {
            log.warn("No currency codes present or currencies is not in allowed list");
            throw new NoSuchElementException("Currency codes is empty or currency is not allowed list. " +
                    "Try to GET currencies to see allowed values");
        }
        List<CurrencyRateDto> currencyRateDtos = currencyExchangeDataService.getExchangeCurrencyRate(currency.getCode(),
                codes);
        currencyRateRepository.saveAll(CurrencyRateDto.from(currencyRateDtos, currency));
        return currencyRateDtos;
    }

    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public void removeRatesBySource(String code) {
        boolean currencyExistsInRatesSource = currencyRateRepository.existsBySource(code);
        if (currencyExistsInRatesSource) {
            log.info(String.format("Removed rates by source [%s]", code));
            currencyRateRepository.removeAllBySource(code);
        }
    }

}
