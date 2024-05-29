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
import java.util.Collections;
import java.util.List;

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
                                              @RequestParam String dateTo) {
        String targetCode = code.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);
        List<String> currencyCodes = currencyService.getCurrencies().stream()
                .map(Currency::getCode)
                .filter(cCode -> !cCode.equals(targetCode))
                .toList();
        RequestPeriod requestPeriod = prepareRequestedPeriod(from, to);
        if (!requestPeriod.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        List<Rate> rates = currencyRateService.getCurrencyRates(targetCode, currencyCodes, requestPeriod);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
    }

    @GetMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCurrencyRates(@RequestParam String code,
                                                   @RequestParam List<String> currencies) {

        String targetCode = code.toUpperCase();
        List<String> currenciesToFetch = new ArrayList<>(currencies);
        currenciesToFetch.replaceAll(String::toUpperCase);
        LocalDate prevDay = LocalDate.now().minusDays(1);
        RequestPeriod requestPeriod = prepareRequestedPeriod(prevDay, prevDay);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(getRates(targetCode, currenciesToFetch,
                requestPeriod)));
    }

    private List<Rate> getRates(String targetCode, List<String> currenciesToFetch, RequestPeriod requestPeriod) {
        List<Rate> fetchedRates;
        List<Rate> rates = currencyRateService.getCurrencyRates(targetCode, currenciesToFetch, requestPeriod);
        if (rates.isEmpty()) {
            fetchedRates = currencyRateService.fetchRates(targetCode, currenciesToFetch);
        } else {
            List<String> sourceCurrencyCodes = rates.stream()
                    .map(r -> r.getSource().getCode())
                    .toList();
            currenciesToFetch.addAll(sourceCurrencyCodes);
            List<String> remainingCurrencyCodes = currenciesToFetch.stream()
                    .filter(rcc -> Collections.frequency(currenciesToFetch, rcc) == 1)
                    .toList();
            if (!remainingCurrencyCodes.isEmpty()) {
                fetchedRates = currencyRateService.fetchRates(targetCode, remainingCurrencyCodes);
                fetchedRates.addAll(rates);
            } else {
                return rates;
            }
        }
        return fetchedRates;
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
