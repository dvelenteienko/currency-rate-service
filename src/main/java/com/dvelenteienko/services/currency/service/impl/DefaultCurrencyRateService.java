package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
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
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

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
    public List<Rate> fetchRates(String baseCurrency, List<String> codes) {
        if (StringUtils.isBlank(baseCurrency)) {
            log.warn("Cannot perform request. Base currency is empty");
            throw new NoSuchElementException("Cannot perform request. Base currency is empty");
        }
        List<Rate> currencyRates = CurrencyMapper.INSTANCE.currencyRatesDtosToRates(currencyExchangeDataService.getExchangeCurrencyRate(baseCurrency,
                codes));
        List<String> currencyCodes = ListUtils.union(codes, List.of(baseCurrency));
        Map<String, Currency> currencyCodeToUUID = currencyRepository.getAllByCodeIn(currencyCodes).stream()
                .collect(Collectors.toMap(Currency::getCode, identity()));
        currencyRates.forEach(rate -> {
            rate.setBase(currencyCodeToUUID.get(baseCurrency));
            rate.setSource(currencyCodeToUUID.get(rate.getSource().getCode()));
        });
        currencyRateRepository.saveAll(currencyRates);
        return currencyRates;
    }

    @Override
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME, keyGenerator = "ratesKeyGenerator")
    public List<Rate> getCurrencyRates(String baseCurrency, List<String> sourceCurrencies, RequestPeriod requestPeriod) {
        List<Rate> rates = currencyRateRepository.findAllByDateBetweenOrderByDateDesc(requestPeriod.getFrom(),
                requestPeriod.getTo());
        return rates.stream()
                .filter(bc -> baseCurrency.equals(bc.getBase().getCode()) &&
                        sourceCurrencies.contains(bc.getSource().getCode()))
                .collect(Collectors.toList());
    }
}
