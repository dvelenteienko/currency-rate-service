package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/rates")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;
    private final CurrencyService currencyService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRates(@RequestParam String baseCurrencyCode,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateFrom,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam String dateTo) {
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);

        RequestPeriodDto requestPeriodDto = RequestPeriodDto.builder()
                .setFrom(from.atStartOfDay())
                .setTo(to.atTime(LocalTime.MAX))
                .build();
        if (!requestPeriodDto.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        return ResponseEntity.ok(currencyRateService.getCurrencyRates(baseCurrencyCode, requestPeriodDto));
    }

    @GetMapping(value = "/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchCurrencyRates(@RequestParam String baseCurrency,
                                                @RequestParam List<String> currencies) {

        LocalDate today = LocalDate.now();
        RequestPeriodDto requestPeriod = RequestPeriodDto.builder()
                .setFrom(today.atStartOfDay())
                .setTo(today.atTime(LocalTime.MAX))
                .build();

        List<String> existingCurrencies = currencyService.getCurrencies().stream()
                .map(CurrencyDto::getCode)
                .collect(Collectors.toList());
        currencies.retainAll(existingCurrencies);

        List<CurrencyRateDto> rates = currencyRateService.getCurrencyRates(baseCurrency, requestPeriod);
        boolean containsExisting = rates.stream()
                .map(CurrencyRateDto::getSource)
                .anyMatch(currencies::contains);

        List<CurrencyRateDto> featchedRates = new ArrayList<>();
        if (!containsExisting) {
            featchedRates = currencyRateService.fetchRates(baseCurrency, new HashSet<>(currencies));
        }
        return ResponseEntity.ok(featchedRates);
    }

//    @PostMapping(value = "/exchange/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> triggerCurrencyRatesExchange(@PathVariable String code) {
//        ResponseEntity<?> response;
//        Set<String> baseCurrencyCodes = currencyService.getCurrencyCodes(CurrencyType.BASE);
//        if (!baseCurrencyCodes.contains(code)) {
//            response = new ResponseEntity<>("The currency code " + code + " is not a BASE currency", HttpStatus.BAD_REQUEST);
//        } else {
//            Set<String> sourceCodes = currencyService.getCurrencyCodes(CurrencyType.SOURCE);
//            List<CurrencyRateDto> currencyRateDtos = currencyRateService.populateRate(code, sourceCodes);
//            response = new ResponseEntity<>(currencyRateDtos, HttpStatus.OK);
//        }
//
//        return response;
//    }
}
