package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CustomCacheResolverStub;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import com.dvelenteienko.services.currency.repository.CurrencyRateRepository;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyRateServiceTest {

    @Mock
    private CurrencyExchangeDataService currencyExchangeDataService;
    @Mock
    private CurrencyRateRepository currencyRateRepository;
    @Mock
    private CustomCacheResolverStub currencyCacheResolver;

    @InjectMocks
    private DefaultCurrencyRateService testee;

    @Test
    public void getCurrencyRates_WhenCurrencyRatesArePresent_ThenReturnDto() {
        String baseCode = "EUR";
        final LocalDateTime localDateTime = LocalDateTime.of(2023, 3, 3, 3, 3);
        RequestPeriodDto requestPeriodDto = RequestPeriodDto.builder().setFrom(localDateTime).setTo(localDateTime).build();
        CurrencyRate rate1 = new CurrencyRate(UUID.randomUUID(), "USD", "EUR", localDateTime, 0.1);
        CurrencyRate rate2 = new CurrencyRate(UUID.randomUUID(), "USD", "GPB", localDateTime, 0.2);
        CurrencyRate rate3 = new CurrencyRate(UUID.randomUUID(), "USD", "EUR", localDateTime, 0.1);
        List<CurrencyRate> rates = List.of(rate1, rate2, rate3);
        when(currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(baseCode, localDateTime, localDateTime))
                .thenReturn(rates);

        List<CurrencyRateDto> expected = testee.getCurrencyRates(baseCode, requestPeriodDto);

        assertThat(expected).hasSize(2);
        assertFalse(expected.contains(rate3));
    }

    @Test
    public void getCurrencyRates_WhenCurrencyRatesAreNotPresent_ThenReturnEmptyList() {
        String baseCode = "EUR";
        final LocalDateTime localDateTime = LocalDateTime.of(2023, 3, 3, 3, 3);
        RequestPeriodDto requestPeriodDto = RequestPeriodDto.builder().setFrom(localDateTime).setTo(localDateTime).build();
        when(currencyRateRepository.findAllByBaseAndDateBetweenOrderByDateDesc(baseCode, localDateTime, localDateTime))
                .thenReturn(Collections.emptyList());

        List<CurrencyRateDto> expected = testee.getCurrencyRates(baseCode, requestPeriodDto);

        assertThat(expected).isNotNull();
        assertThat(expected).hasSize(0);
    }

    @Test
    public void createCurrencyRate_WhenAllParametersArePresent_ThenCreateAndReturn() {
        String baseCode = "EUR";
        Set<String> codes = Set.of("USD");
        final LocalDateTime localDateTime = LocalDateTime.of(2023, 3, 3, 3, 3);
        CurrencyRateDto currencyRateDto = CurrencyRateDto.builder()
                .setBase(baseCode)
                .setSource("USD")
                .setDate(localDateTime)
                .setRate(0.1)
                .build();
        List<CurrencyRate> currencyRates = CurrencyRateDto.fromDto(List.of(currencyRateDto));
        when(currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes)).thenReturn(List.of(currencyRateDto));

        List<CurrencyRateDto> expected = testee.createCurrencyRate(baseCode, codes);

        assertThat(expected).isNotNull();
        assertThat(expected).hasSize(1);
        assertTrue(expected.contains(currencyRateDto));
        verify(currencyRateRepository).saveAllAndFlush(currencyRates);
    }

    @Test
    public void createCurrencyRate_WhenCurrencyCodesAreNotPresent_ThenAddBaseCodeCreateAndReturn() {
        String baseCode = "EUR";
        Set<String> codes = Set.of("EUR");
        Set<String> codesMethodParam = new HashSet<>();
        final LocalDateTime localDateTime = LocalDateTime.of(2023, 3, 3, 3, 3);
        CurrencyRateDto currencyRateDto = CurrencyRateDto.builder()
                .setBase(baseCode)
                .setSource(baseCode)
                .setDate(localDateTime)
                .setRate(0.1)
                .build();
        List<CurrencyRate> currencyRates = CurrencyRateDto.fromDto(List.of(currencyRateDto));
        when(currencyExchangeDataService.getExchangeCurrencyRate(baseCode, codes)).thenReturn(List.of(currencyRateDto));

        List<CurrencyRateDto> expected = testee.createCurrencyRate(baseCode, codesMethodParam);

        assertThat(expected).isNotNull();
        assertThat(expected).hasSize(1);
        assertTrue(expected.contains(currencyRateDto));
        assertThat(expected.get(0).getSource()).isEqualTo(baseCode);
        verify(currencyRateRepository).saveAllAndFlush(currencyRates);
    }

}
