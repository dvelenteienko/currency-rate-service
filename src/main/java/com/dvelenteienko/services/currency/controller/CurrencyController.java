package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
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
        if (showType) {
            responseEntity = new ResponseEntity<>(currencyDtos, HttpStatus.OK);
        } else {
            Set<String> codes = currencyDtos.stream()
                    .map(CurrencyDto::getCode)
                    .collect(Collectors.toSet());
            responseEntity = new ResponseEntity<>(codes, HttpStatus.OK);
        }
        return responseEntity;
    }

    @Operation(description = "Note: if type does not present then 'SOURCE' will be applied")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCurrency(@RequestBody CurrencyDto currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        CurrencyDto currencyDto = currencyService.createCurrency(currencyRequest.getCode(), currencyType);
        if (currencyDto == null) {
            return new ResponseEntity<>("The currency with code " + currencyRequest.getCode() +
                    " already exist", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(currencyDto, HttpStatus.CREATED);
    }

    @Operation(description = "Note: if type does not present then 'SOURCE' will be applied")
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCurrency(@RequestBody CurrencyDto currencyRequest) {
        CurrencyType currencyType = getCurrencyType(currencyRequest);
        CurrencyDto currency = currencyService.updateCurrency(currencyRequest.getCode(), currencyType);
        if (currency == null) {
            return new ResponseEntity<>("The currency with code " + currencyRequest.getCode() +
                    " and type " + currencyRequest.getType().toString() +
                    " already present", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(currency, HttpStatus.OK);
    }

    private CurrencyType getCurrencyType(CurrencyDto currencyRequest) {
        CurrencyType currencyType = currencyRequest.getType();
        if (currencyType == null) {
            currencyType = CurrencyType.SOURCE;
        }
        return currencyType;
    }

}
