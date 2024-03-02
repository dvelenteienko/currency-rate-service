package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public Set<String> getCurrencyCodes() {
        return currencyRepository.getCodes();
    }

    @Override
    public List<Currency> getCurrencies() {
        return currencyRepository.getCodesWithType();
    }

    @Override
    public Currency createCurrency(String code, CurrencyType type) {
        Currency currency = null;
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isEmpty()) {
            currency = Currency.builder()
                    .setCode(code)
                    .setType(type)
                    .build();
            updateBaseType(currency);
        }
        return currency;
    }

    @Override
    public Currency updateCurrencyTypeByCode(String code, CurrencyType type) {
        Currency currency = null;
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isPresent()) {
            Currency currencyFrom = currencyOpt.get();
            Currency currencyToUpdate = Currency.builder()
                    .setId(currencyFrom.getId())
                    .setCode(currencyFrom.getCode())
                    .setType(type)
                    .build();

            boolean invalidCurrencyTypeToUpdate = currencyFrom.getType() == CurrencyType.BASE && type != CurrencyType.BASE;
            if (currencyFrom.getType() == type && currencyFrom.getCode().equals(code)) {
                currency = currencyFrom;
            } else if (currencyFrom.getCode().equals(code) && invalidCurrencyTypeToUpdate) {
                currency = currencyFrom;
            } else {
                updateBaseType(currencyToUpdate);
                currency = currencyToUpdate;
            }
        }
        return currency;
    }

    private void updateBaseType(Currency sourceCurrency) {
        if (sourceCurrency.getType() == CurrencyType.BASE) {
            Optional<Currency> baseTypeCurrencyOpt = currencyRepository.findTopByType(CurrencyType.BASE);
            if (baseTypeCurrencyOpt.isPresent()) {
                Currency currency = baseTypeCurrencyOpt.get();
                Currency currencyToSave = Currency.builder()
                        .setId(currency.getId())
                        .setCode(currency.getCode())
                        .setType(CurrencyType.SOURCE)
                        .build();
                currencyRepository.save(currencyToSave);
            }
        }
        currencyRepository.save(sourceCurrency);
    }
}
