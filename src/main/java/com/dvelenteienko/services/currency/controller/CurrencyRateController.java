package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.dvelenteienko.services.currency.util.RequestPeriod;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/rate")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;
    private final CurrencyService currencyService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencyRates(@RequestParam @NotBlank String code,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam @NotBlank String dateFrom,
                                              @Parameter(description = "Note: date format must be 'yyyy-mm-dd'")
                                              @RequestParam @NotBlank String dateTo) {
        String targetCode = code.toUpperCase();
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);
        List<String> currencyCodes = getCurrencyCodes(Collections.emptyList(), targetCode);
        RequestPeriod requestPeriod = prepareRequestedPeriod(from, to);
        if (!requestPeriod.isValid()) {
            throw new IllegalArgumentException(String.format("Date range is not valid. From: [%s] To:[%s]", dateFrom, dateTo));
        }
        List<Rate> rates = currencyRateService.getRates(targetCode, currencyCodes, requestPeriod);
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
    }

    @GetMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCurrencyRates(@RequestParam @NotBlank String code,
                                                   @RequestParam List<@Size(min = 1) String> currencies) {

        String targetCode = code.toUpperCase();
        List<String> currenciesToFetch = new ArrayList<>(currencies);
        currenciesToFetch.replaceAll(String::toUpperCase);
        LocalDate prevDay = LocalDate.now().minusDays(1);
        RequestPeriod requestPeriod = prepareRequestedPeriod(prevDay, prevDay);
        List<String> currencyCodes = getCurrencyCodes(currenciesToFetch, targetCode);
        List<Rate> rates = currencyRateService.persisRates(targetCode, currencyCodes, requestPeriod);

        return ResponseEntity.ok(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));
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

    private List<String> getCurrencyCodes(List<String> currencyCodes, String baseCode) {
        if (currencyCodes.contains(baseCode)) {
            throw new IllegalArgumentException(String.format("Cannot get rates of itself code [%s]", baseCode));
        }
        List<String> currencies = currencyService.getCurrencies().stream()
                .map(Currency::getCode)
                .toList();
        List<String> notInCommonCodes = currencyCodes.stream()
                .filter(c -> !currencies.contains(c))
                .toList();
        if (!notInCommonCodes.isEmpty()) {
            throw new NoSuchElementException(String.format("Currency [%s] does not exist. Existing currencies [%s] ",
                    String.join(",", notInCommonCodes),
                    String.join(",", currencies)));
        }
        Predicate<String> filterCodesPredicate;
        if (currencyCodes.isEmpty()) {
            filterCodesPredicate = c -> !baseCode.equals(c);
        } else {
            filterCodesPredicate = c -> !baseCode.equals(c) && currencyCodes.contains(c);
        }
        return currencies.stream()
                .filter(filterCodesPredicate)
                .toList();
    }

}
