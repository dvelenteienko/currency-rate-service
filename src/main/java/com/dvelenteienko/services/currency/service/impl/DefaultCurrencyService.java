package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CustomCacheResolver;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private CustomCacheResolver currencyCacheResolver;


    @Override
    @Transactional(readOnly = true, value = "transactionManager")
    public List<CurrencyDto> getCurrencies() {
        List<CurrencyDto> currencies = currencyCacheResolver.getFromCache();
        if (currencies.isEmpty()) {
            currencies = currencyCacheResolver.putToCache(CurrencyDto.fromAll(currencyRepository.findAll()));
        }
        return currencies;
    }

    @Override
    public Set<String> getCurrencyCodes(CurrencyType type) {
        return getCurrencies().stream()
                .filter(c -> c.getType() == type)
                .map(CurrencyDto::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional("transactionManager")
    public CurrencyDto createCurrency(String code, CurrencyType type) {
        CurrencyDto currencyDto = null;
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isEmpty()) {
            Currency currency = new Currency(null, code, type);
            updateBaseType(currency);
            currencyDto = CurrencyDto.from(currency);
            currencyCacheResolver.putToCache(List.of(currencyDto));
        }
        return currencyDto;
    }

    @Override
    @Transactional("transactionManager")
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
            currencyCacheResolver.putToCache(List.of(currencyDto));
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
