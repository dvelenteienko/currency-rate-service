package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.dvelenteienko.services.currency.util.RequestPeriod;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/rate")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;
    private final CurrencyService currencyService;

    @GetMapping(value = "base", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRatesByBase(@RequestParam String code,
                                                    @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                                    @RequestParam String dateFrom,
                                                    @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                                    @RequestParam String dateTo) {
        String targetCode = code.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);
        Currency currency = currencyService.getCurrencyByCode(targetCode);

        RequestPeriod requestPeriod = prepareRequestedPeriod(from, to);
        if (!requestPeriod.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        List<Rate> rates = currencyRateService.getCurrencyRatesByBase(currency, requestPeriod);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
    }

    @GetMapping(value = "/source", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRatesBySource(@RequestParam String code,
                                                      @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                                      @RequestParam String dateFrom,
                                                      @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                                      @RequestParam String dateTo) {
        String targetCode = code.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);
        Currency currency = currencyService.getCurrencyByCode(targetCode);

        RequestPeriod requestPeriod = prepareRequestedPeriod(from, to);
        if (!requestPeriod.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        List<Rate> rates = currencyRateService.getCurrencyRatesBySource(currency, requestPeriod);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
    }

    @GetMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCurrencyRates(@RequestParam String code,
                                                   @RequestParam List<String> currencies) {

        String targetCode = code.toUpperCase();
        Currency currency = currencyService.getCurrencyByCode(code);

        LocalDate prevDay = LocalDate.now().minusDays(1);
        RequestPeriod requestPeriod = prepareRequestedPeriod(prevDay, prevDay);
        List<String> normalizedCurrencies = normalizeCurrencies(currencies);
        boolean containsExisting = isContainsExistingRates(requestPeriod, normalizedCurrencies);

        if (containsExisting) {
            throw new IllegalArgumentException(
                    String.format("The following currency rates [%s] of base currency code [%s] already exists",
                            String.join(",", normalizedCurrencies), targetCode));
        }
        List<Rate> rates = currencyRateService.fetchRates(currency, normalizedCurrencies);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
    }

    private boolean isContainsExistingRates(RequestPeriod requestPeriod, List<String> normalizedCurrencies) {
        List<Rate> rates = currencyRateService.getCurrencyRatesByPeriod(requestPeriod);
        return !rates.isEmpty() && rates.stream()
                .map(rdto -> rdto.getSource().getCode())
                .allMatch(normalizedCurrencies::contains);
    }

    private List<String> normalizeCurrencies(List<String> currencyList) {
        List<String> currencies = new ArrayList<>(currencyList);
        List<String> existingCurrencies = currencyService.getCurrencies().stream()
                .map(Currency::getCode)
                .collect(Collectors.toList());
        currencies.replaceAll(String::toUpperCase);
        currencies.retainAll(existingCurrencies);
        return currencies;
    }

    private RequestPeriod prepareRequestedPeriod(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDateTime endOfDay = to.atTime(LocalTime.MAX);
        if (today.equals(to)) {
            endOfDay = LocalDateTime.now();
        }
        return RequestPeriod.builder()
                .setFrom(from.atStartOfDay())
                .setTo(endOfDay)
                .build();
    }

}
