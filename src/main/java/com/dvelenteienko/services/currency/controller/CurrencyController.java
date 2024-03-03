package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrencies(@RequestParam(required = false) boolean showType) {
        ResponseEntity<?> responseEntity;
        List<CurrencyDto> currencyDtos = currencyService.getCurrencies();
        Set<String> codes = currencyDtos.stream()
                .map(CurrencyDto::getCode)
                .collect(Collectors.toSet());
        responseEntity = showType ? new ResponseEntity<>(currencyDtos, HttpStatus.OK)
                 : new ResponseEntity<>(codes, HttpStatus.OK);
        return responseEntity;

    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CurrencyDto addCurrency(@RequestBody CurrencyDto currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        CurrencyDto currency = currencyService.createCurrency(currencyRequest.getCode(), currencyType);
        if (currency == null) {
            return currencyRequest;
        }
        return currency;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CurrencyDto updateCurrency(@RequestBody CurrencyDto currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        CurrencyDto currency = currencyService.updateCurrency(currencyRequest.getCode(), currencyType);
        if (currency == null) {
            return currencyRequest;
        }
        return currency;
    }

    private CurrencyType getCurrencyType(CurrencyDto currencyRequest) {
        CurrencyType currencyType = currencyRequest.getType();
        if (currencyType == null) {
            currencyType = CurrencyType.SOURCE;
        }
        return currencyType;
    }
    
}
