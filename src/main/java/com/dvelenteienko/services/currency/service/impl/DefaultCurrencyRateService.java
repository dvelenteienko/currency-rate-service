package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
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
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;

    @Override
    @Cacheable(value = CacheConfig.RATE_CACHE_NAME,
            key = "T(java.lang.String).format('%s-%s-%s-%s', #type, #baseCode, #requestPeriod.from, #requestPeriod.to)")
    public List<CurrencyRateDto> getCurrencyRates(String baseCode, RequestPeriodDto requestPeriod, CurrencyType type) {
        List<CurrencyRate> currencyRates;
        if(CurrencyType.BASE == type) {
            currencyRates =
                    currencyRateRepository.findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc(Currency.builder()
                                    .code(baseCode).build(), requestPeriod.getFrom(), requestPeriod.getTo());
        } else {
            currencyRates = currencyRateRepository.findAllBySourceAndDateBetweenOrderByDateDesc(Currency.builder()
                    .code(baseCode).build(), requestPeriod.getFrom(), requestPeriod.getTo());
        }
        log.info("Getting currency rates: {}", currencyRates.size());
        return CurrencyRateDto.toDto(currencyRates).stream().distinct().toList();
    }

    @Override
    @CacheEvict(value = CacheConfig.RATE_CACHE_NAME, allEntries = true)
    public List<CurrencyRateDto> fetchRates(String baseCode, Set<String> codes) {
        if (codes.isEmpty()) {
            log.warn("No currency codes present!");
            throw new NoSuchElementException("Currency codes is empty");
        }
        List<CurrencyRateDto> currencyRateDtos = currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes);
        currencyRateRepository.saveAll(CurrencyRateDto.fromDto(currencyRateDtos));
        return currencyRateDtos;
    }
}
