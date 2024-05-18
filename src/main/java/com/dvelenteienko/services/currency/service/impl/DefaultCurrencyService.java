package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Cacheable(value = CacheConfig.CURRENCY_CACHE_NAME)
    public List<CurrencyDto> getCurrencies() {
        return CurrencyDto.from(currencyRepository.findAll());
    }

    @Override
    @CacheEvict(value = CacheConfig.CURRENCY_CACHE_NAME)
    public CurrencyDto createCurrency(String code, CurrencyType type) {
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isPresent()) {
            throw new IllegalArgumentException(String.format("Currency '%s' already exists", code));
        }
        Currency currency = Currency.builder()
                .code(code)
                .type(type)
                .build();
        Currency saved = currencyRepository.save(currency);
        return CurrencyDto.from(saved);
    }

    @Override
    @CacheEvict(value = CacheConfig.CURRENCY_CACHE_NAME)
    public void removeCurrency(String code) {
        Optional<Currency> optionalCurrency = currencyRepository.findTopByCode(code);
        if (optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();
            log.info(String.format("Removed '%s' currency ", currency.getCode()));
            currencyRepository.delete(currency);
        }
    }

}
