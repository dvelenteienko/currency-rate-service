package com.dvelenteienko.services.currency.service.impl;

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

@Slf4j
@Service
@AllArgsConstructor
public class DefaultCurrencyService implements CurrencyService {

    private final CurrencyRepository currencyRepository;


    @Override
    @Transactional(readOnly = true, value = "transactionManager")
    public List<CurrencyDto> getCurrencies() {
        return CurrencyDto.from(currencyRepository.findAll());
    }

    @Override
    @Transactional("transactionManager")
    public CurrencyDto createCurrency(String code, CurrencyType type) {
        Optional<Currency> currencyOpt = currencyRepository.findTopByCode(code);
        if (currencyOpt.isPresent()) {
            throw new IllegalArgumentException(String.format("Currency '%s' already exists", code));
        }
        Currency currency = Currency.builder()
                .code(code)
                .type(type)
                .build();
        return CurrencyDto.from(currency);
    }

    @Override
    public void removeCurrency(String code) {
        Long deletedCount = currencyRepository.deleteByCode(code);
        log.info(String.format("Removed '%d' record(s)", deletedCount));
    }

}
