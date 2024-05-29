package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Cacheable(value = CacheConfig.CURRENCY_CACHE_NAME)
    public List<Currency> getCurrencies() {
        return currencyRepository.findAll();
    }

//    @Override
//    public Currency getCurrencyByCode(String code) {
//        Currency currency = currencyRepository.getByCode(code);
//        if (currency != null) {
//            return currency;
//        }
//        return Currency.builder().build();
//    }

    @Override
    @CacheEvict(value = CacheConfig.CURRENCY_CACHE_NAME, allEntries = true)
    public Currency createCurrency(String code) {
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isPresent()) {
            throw new IllegalArgumentException(String.format("Currency '%s' already exists", code));
        }
        Currency currency = Currency.builder()
                .setCode(code)
                .build();
        return currencyRepository.save(currency);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CURRENCY_CACHE_NAME, allEntries = true),
            @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    })
    public void removeCurrency(Currency currency) {
        log.info(String.format("Removed [%s]", currency));
        currencyRepository.delete(currency);
    }

//    @Override
//    public List<Currency> getCurrencyByCodes(List<String> currencyCodes) {
//        return getCurrencies().stream()
//                .filter(c -> currencyCodes.contains(c.getCode()))
//                .collect(Collectors.toList());
//    }
}
