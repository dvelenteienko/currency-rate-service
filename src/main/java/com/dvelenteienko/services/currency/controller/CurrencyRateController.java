package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRates(@RequestParam String code,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateFrom,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateTo,
                                              @RequestParam(defaultValue = "BASE") CurrencyType target) {
        String targetCode = code.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);
        Currency currency = CurrencyDto.from(currencyService.getCurrencyByCode(targetCode));

        RequestPeriodDto requestPeriodDto = prepareRequestedPeriod(from, to);
        if (!requestPeriodDto.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        return ResponseEntity.ok(currencyRateService.getCurrencyRates(currency, requestPeriodDto, target));
    }

    @GetMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCurrencyRates(@RequestParam String code,
                                                   @RequestParam List<String> currencies) {

        String targetCode = code.toUpperCase();
        Currency currency = CurrencyDto.from(currencyService.getCurrencyByCode(code));

        LocalDate prevDay = LocalDate.now().minusDays(1);
        RequestPeriodDto requestPeriod = prepareRequestedPeriod(prevDay, prevDay);
        List<String> normalizedCurrencies = normalizeCurrencies(currencies);
        boolean containsExisting = isContainsExistingRates(currency, requestPeriod, normalizedCurrencies);

        if (containsExisting) {
            throw new IllegalArgumentException(
                    String.format("The following currency rates [%s] of base currency code [%s] already exists",
                            String.join(",", normalizedCurrencies), targetCode));
        }
        return ResponseEntity.ok(currencyRateService.fetchRates(currency, normalizedCurrencies));
    }

    private boolean isContainsExistingRates(Currency currency, RequestPeriodDto requestPeriod, List<String> normalizedCurrencies) {
        List<CurrencyRateDto> rates = currencyRateService.getCurrencyRates(currency, requestPeriod, CurrencyType.BASE);
        return !rates.isEmpty() && rates.stream()
                .map(CurrencyRateDto::getSource)
                .allMatch(normalizedCurrencies::contains);
    }

    private List<String> normalizeCurrencies(List<String> currencyList) {
        List<String> currencies = new ArrayList<>(currencyList);
        List<String> existingCurrencies = currencyService.getCurrencies().stream()
                .map(CurrencyDto::getCode)
                .collect(Collectors.toList());
        currencies.replaceAll(String::toUpperCase);
        currencies.retainAll(existingCurrencies);
        return currencies;
    }

    private RequestPeriodDto prepareRequestedPeriod(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDateTime endOfDay = to.atTime(LocalTime.MAX);
        if (today.equals(to)) {
            endOfDay = LocalDateTime.now();
        }
        return RequestPeriodDto.builder()
                .setFrom(from.atStartOfDay())
                .setTo(endOfDay)
                .build();
    }

}
