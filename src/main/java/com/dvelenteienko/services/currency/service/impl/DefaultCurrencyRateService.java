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
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.UnaryOperator;
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
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME, keyGenerator = "ratesKeyGenerator")
    public List<Rate> getRates(String baseCurrency, List<String> sourceCurrencies, RequestPeriod requestPeriod) {
        return getCurrentRates(baseCurrency, sourceCurrencies, requestPeriod);
    }

    //    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public List<Rate> persisRates(String baseCurrency, List<String> codes, RequestPeriod requestPeriod) {
        if (StringUtils.isBlank(baseCurrency)) {
            log.warn("Cannot perform request. Base currency is empty");
            throw new NoSuchElementException("Cannot perform request. Base currency is empty");
        }
        List<Rate> rates = getActualRates(baseCurrency, codes, requestPeriod);
        saveRates(rates);
        return rates;
    }

    @Override
    public void fetchAndPersistRates(String baseCode, List<String> codes) {
        List<Rate> rates = fetchRates(baseCode, codes);
        saveRates(rates);
    }

    private void saveRates(List<Rate> rates) {
        currencyRateRepository.saveAll(rates);
    }

    private List<Rate> fetchRates(String baseCurrency, List<String> codes) {
        List<Rate> currencyRates = mapCurrencyRates(baseCurrency, codes);
        Map<String, Currency> currencyMap = getCurrencyMap(baseCurrency, codes);
        updateRateCurrencies(baseCurrency, currencyRates, currencyMap);
        return currencyRates;
    }

    private List<Rate> getCurrentRates(String baseCurrency, List<String> sourceCurrencies, RequestPeriod requestPeriod) {
        List<Rate> rates = currencyRateRepository.findAllByDateBetweenOrderByDateDesc(requestPeriod.getFrom(),
                requestPeriod.getTo());
        return rates.stream()
                .filter(bc -> baseCurrency.equals(bc.getBase().getCode()) &&
                        sourceCurrencies.contains(bc.getSource().getCode()))
                .toList();
    }

    private List<Rate> getActualRates(String targetCode, List<String> currenciesToFetch, RequestPeriod requestPeriod) {
        List<Rate> fetchedRates;
        List<Rate> rates = getCurrentRates(targetCode, currenciesToFetch, requestPeriod);
        if (rates.isEmpty()) {
            fetchedRates = fetchRates(targetCode, currenciesToFetch);
        } else {
            List<String> sourceCurrencyCodes = rates.stream()
                    .map(r -> r.getSource().getCode())
                    .toList();
            Set<String> currenciesToRemain = new HashSet<>(currenciesToFetch);
            currenciesToRemain.addAll(sourceCurrencyCodes);
            List<String> remainingCurrencyCodes = currenciesToRemain.stream()
                    .filter(rcc -> rates.stream().noneMatch(r -> rcc.equals(r.getSource().getCode())))
                    .toList();
            if (!remainingCurrencyCodes.isEmpty()) {
                fetchedRates = fetchRates(targetCode, remainingCurrencyCodes);
                fetchedRates.addAll(rates);
            } else {
                fetchedRates = rates;
            }
        }
        return Collections.unmodifiableList(fetchedRates);
    }

    private List<Rate> mapCurrencyRates(String baseCurrency, List<String> codes) {
        return CurrencyMapper.INSTANCE.currencyRatesDtosToRates(
                currencyExchangeDataService.getExchangeCurrencyRate(baseCurrency, codes)
        );
    }

    private Map<String, Currency> getCurrencyMap(String baseCurrency, List<String> codes) {
        List<String> currencyCodes = new ArrayList<>(codes);
        currencyCodes.add(baseCurrency);
        return currencyRepository.getAllByCodeIn(currencyCodes).stream()
                .collect(Collectors.toMap(Currency::getCode, UnaryOperator.identity()));
    }

    private void updateRateCurrencies(String baseCurrency, List<Rate> currencyRates, Map<String, Currency> currencyMap) {
        currencyRates.forEach(rate -> {
            rate.setBase(currencyMap.get(baseCurrency));
            rate.setSource(currencyMap.get(rate.getSource().getCode()));
        });
    }
}
