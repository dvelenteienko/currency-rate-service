package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/currencies")
public class SimpleController {

    private final CurrencyExchangeDataService currencyExchangeDataService;
    private final CurrencyService currencyService;

    @GetMapping(path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CurrencyRateDto> getCurrencies() {
        return currencyExchangeDataService.getCurrencyRateDtos("USD", Set.of("EUR", "GBP"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCurrencies(@RequestParam(required = false) boolean showType) {
        ResponseEntity<?> responseEntity;
        responseEntity = showType ? new ResponseEntity<>(currencyService.getCurrencies(), HttpStatus.OK)
                 : new ResponseEntity<>(currencyService.getCurrencyCodes(), HttpStatus.OK);
        return responseEntity;

    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Currency addCurrency(@RequestBody Currency currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        Currency currency = currencyService.createCurrency(currencyRequest.getCode(), currencyType);
        if (currency == null) {
            return currencyRequest;
        }
        return currency;
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Currency updateCurrency(@RequestBody Currency currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        Currency currency = currencyService.updateCurrencyTypeByCode(currencyRequest.getCode(), currencyType);
        if (currency == null) {
            return currencyRequest;
        }
        return currency;
    }

    private CurrencyType getCurrencyType(Currency currencyRequest) {
        CurrencyType currencyType = currencyRequest.getType();
        if (currencyType == null) {
            currencyType = CurrencyType.SOURCE;
        }
        return currencyType;
    }
    
}
