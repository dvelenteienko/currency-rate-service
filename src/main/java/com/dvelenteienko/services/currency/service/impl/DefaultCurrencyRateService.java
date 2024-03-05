package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CustomCacheResolver;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@Transactional("transactionManager")
public class DefaultCurrencyRateService implements CurrencyRateService {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyRateRepository currencyRateRepository;
    private final CustomCacheResolver rateCacheResolver;

    @Override
    public List<CurrencyRateDto> getCurrencyRates(String baseCode, RequestPeriodDto requestPeriod) {
        List<CurrencyRateDto> currencyRateDtos = rateCacheResolver.getFromCache();
        if (currencyRateDtos.isEmpty()) {
            List<CurrencyRate> currencyRates = currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(baseCode,
                    requestPeriod.getFrom(), requestPeriod.getTo());
            log.info("Getting currency rates: {}", currencyRates.size());
            currencyRateDtos = CurrencyRateDto.toDto(currencyRates).stream()
                    .distinct()
                    .toList();
            log.info("Removed duplicates. Currency rates: {}", currencyRateDtos.size());
            rateCacheResolver.putToCache(currencyRateDtos);
        }
        return currencyRateDtos;
    }

    @Override
    public List<CurrencyRateDto> createCurrencyRate(String baseCode, Set<String> codes) {
        if (codes.isEmpty()) {
            codes.add(baseCode);
        }
        List<CurrencyRateDto> currencyRateDtos = currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes);
        currencyRateRepository.saveAllAndFlush(CurrencyRateDto.fromDto(currencyRateDtos));
        rateCacheResolver.putToCache(currencyRateDtos);
        return currencyRateDtos;
    }

}
