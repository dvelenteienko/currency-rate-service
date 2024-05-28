package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.util.RequestPeriod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public List<Rate> fetchRates(Currency currency, List<String> codes) {
        if (codes.isEmpty() || currency == null) {
            log.warn("No currency codes present or currencies is not in allowed list");
            throw new NoSuchElementException("Currency codes is empty or currency is not allowed list. " +
                    "Try to GET currencies to see allowed values");
        }
        List<Rate> rates = currencyExchangeDataService.getExchangeCurrencyRate(currency.getCode(),
                codes);
        Map<String, UUID> currencyCodeToUUID = currencyRepository.getAllByCodeIn(codes).stream()
                .collect(Collectors.toMap(Currency::getCode, Currency::getId));
        rates.forEach(rate -> {
            if (currencyCodeToUUID.containsKey(rate)) {
                rate.setId(currencyCodeToUUID.get(rate.getSource()));
            }
        });
        currencyRateRepository.saveAll(rates);
        return rates;
    }

    @Override
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME,
            key = "T(java.lang.String).format('%s-%s-%s-%s', 'BASE', #currency.code, #requestPeriod.from, #requestPeriod.to)")
    public List<Rate> getCurrencyRatesByBase(Currency currency, RequestPeriod requestPeriod) {
        List<Rate> rates = currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(currency,
                requestPeriod.getFrom(),
                requestPeriod.getTo());
        log.info("Getting currency rates by base: {}", rates.size());
        return rates;
    }

    @Override
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME,
            key = "T(java.lang.String).format('%s-%s-%s-%s', 'SOURCE', #currency.code, #requestPeriod.from, #requestPeriod.to)")
    public List<Rate> getCurrencyRatesBySource(Currency currency, RequestPeriod requestPeriod) {
        List<Rate> rates = currencyRateRepository.findAllBySourceAndDateBetweenOrderByDateDesc(currency, requestPeriod.getFrom(), requestPeriod.getTo());
        log.info("Getting currency rates by source: {}", rates.size());
        return rates;
    }

    @Override
    public List<Rate> getCurrencyRates(Currency baseCurrency, List<Currency> sourceCurrencies, RequestPeriod requestPeriod) {
        return null;
    }

    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public void removeRatesBySource(String code) {
        //not implemeted
    }

    @Override
    public List<Rate> getCurrencyRatesByPeriod(RequestPeriod requestPeriod) {
        return currencyRateRepository.findAllByDateBetweenOrderByDateDesc(requestPeriod.getFrom(), requestPeriod.getTo());
    }
}
