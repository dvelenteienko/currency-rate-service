package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/rate")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;
    private final CurrencyService currencyService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRates(@RequestParam String baseCurrencyCode,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateFrom,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateTo,
                                              @RequestParam(required = false, defaultValue = "base") CurrencyType target) {
        baseCurrencyCode = baseCurrencyCode.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);

        RequestPeriodDto requestPeriodDto = prepareRequestedPeriod(from, to);
        if (!requestPeriodDto.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        return ResponseEntity.ok(currencyRateService.getCurrencyRates(baseCurrencyCode, requestPeriodDto, target));
    }

    @GetMapping(value = "/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchCurrencyRates(@RequestParam String baseCurrency,
                                                @RequestParam List<String> currencies) {

        baseCurrency = baseCurrency.toUpperCase();
        LocalDate prevDay = LocalDate.now().minusDays(1);
        RequestPeriodDto requestPeriod = prepareRequestedPeriod(prevDay, prevDay);

        List<String> existingCurrencies = currencyService.getCurrencies().stream()
                .map(CurrencyDto::getCode)
                .collect(Collectors.toList());
        currencies.replaceAll(String::toUpperCase);
        currencies.retainAll(existingCurrencies);

        List<CurrencyRateDto> rates = currencyRateService.getCurrencyRates(baseCurrency, requestPeriod, CurrencyType.BASE);
        boolean containsExisting = rates.stream()
                .map(CurrencyRateDto::getSource)
                .anyMatch(currencies::contains);

        List<CurrencyRateDto> featchedRates = new ArrayList<>();
        if (!containsExisting) {
            featchedRates = currencyRateService.fetchRates(baseCurrency, new HashSet<>(currencies));
        }
        return ResponseEntity.ok(featchedRates);
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
