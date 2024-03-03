package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.DefaultCacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {DefaultCacheConfig.CURRENCY_CACHE_NAME})
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Cacheable
    @Transactional(readOnly = true, value = "transactionManager")
    public List<CurrencyDto> getCurrencies() {
        return CurrencyDto.fromAll(currencyRepository.findAll());
    }

    @Override
    @Cacheable
    public Set<String> getCurrencyCodes(CurrencyType type) {
        return currencyRepository.getCodesByType(type);
    }

    @Override
    @CacheEvict(value = DefaultCacheConfig.CURRENCY_CACHE_NAME, allEntries = true)
    @Transactional("transactionManager")
    public CurrencyDto createCurrency(String code, CurrencyType type) {
        CurrencyDto currencyDto = null;
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isEmpty()) {
            Currency currency = new Currency(null, code, type);
            updateBaseType(currency);
            currencyDto = CurrencyDto.from(currency);
        }
        return currencyDto;
    }

    @Override
    @Transactional("transactionManager")
    @CacheEvict(value = DefaultCacheConfig.CURRENCY_CACHE_NAME, allEntries = true)
    public CurrencyDto updateCurrency(String code, CurrencyType type) {
        CurrencyDto currencyDto = null;
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isPresent()) {
            Currency currencyFrom = currencyOpt.get();
            Currency currencyToUpdate = new Currency(currencyFrom.getId(), currencyFrom.getCode(), type);
            boolean invalidCurrencyTypeToUpdate = currencyFrom.getType() == CurrencyType.BASE && type != CurrencyType.BASE;
            boolean sameCurrency = currencyFrom.getType() == type && currencyFrom.getCode().equals(code);
            if ((currencyFrom.getCode().equals(code) && invalidCurrencyTypeToUpdate) || sameCurrency) {
                currencyDto = CurrencyDto.from(currencyFrom);
            } else {
                updateBaseType(currencyToUpdate);
                currencyDto = CurrencyDto.from(currencyToUpdate);
            }
        }
        return currencyDto;
    }

    private void updateBaseType(Currency sourceCurrency) {
        if (sourceCurrency.getType() == CurrencyType.BASE) {
            Optional<Currency> baseTypeCurrencyOpt = currencyRepository.findTopByType(CurrencyType.BASE);
            if (baseTypeCurrencyOpt.isPresent()) {
                Currency currency = baseTypeCurrencyOpt.get();
                Currency currencyToSave = new Currency(currency.getId(), currency.getCode(), CurrencyType.SOURCE);
                currencyRepository.save(currencyToSave);
            }
        }
        currencyRepository.save(sourceCurrency);
    }


}
