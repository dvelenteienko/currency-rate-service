package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.DefaultCacheConfig;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
@CacheConfig(cacheNames = {DefaultCacheConfig.RATE_CACHE_NAME})
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;

    @Override
    @Cacheable
    public List<CurrencyRateDto> getCurrencyRates(String baseCode, RequestPeriodDto requestPeriod) {
        List<CurrencyRate> currencyRates = currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(baseCode,
                requestPeriod.getFrom(), requestPeriod.getTo());
        log.info("Getting currency rates: {}", currencyRates.size());
        List<CurrencyRateDto> currencyRateDtos = CurrencyRateDto.toDto(currencyRates).stream()
                .distinct()
                .toList();
        log.info("Removed duplicates. Currency rates: {}", currencyRateDtos.size());
        return currencyRateDtos;
    }

    @Override
    @CacheEvict(value = DefaultCacheConfig.RATE_CACHE_NAME, allEntries=true)
    public List<CurrencyRateDto> createCurrencyRate(String baseCode, Set<String> codes) {
        if (codes.isEmpty()) {
            codes.add(baseCode);
        }
        List<CurrencyRateDto> currencyRateDtos = currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes);
        currencyRateRepository.saveAllAndFlush(CurrencyRateDto.fromDto(currencyRateDtos));
        return currencyRateDtos;
    }
}
