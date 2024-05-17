package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

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
        ResponseEntity<?> responseEntity;
        final LocalDate from = LocalDate.parse(dateFrom);
        final LocalDate to = LocalDate.parse(dateTo);

        RequestPeriodDto requestPeriodDto = RequestPeriodDto.builder()
                .setFrom(from.atStartOfDay())
                .setTo(to.atTime(LocalTime.now()))
                .build();
        if (!requestPeriodDto.isValid()) {
            responseEntity = new ResponseEntity<>("Date range is not valid. From: [" + dateFrom + "] To:[" + dateTo + "]",
                    HttpStatus.BAD_REQUEST);
        } else {
            List<CurrencyRateDto> currencyRateDtos = currencyRateService.getCurrencyRates(baseCurrencyCode, requestPeriodDto);
            responseEntity = new ResponseEntity<>(currencyRateDtos, HttpStatus.OK);
        }
        return responseEntity;
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
