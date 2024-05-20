package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
@RequestMapping(Api.BASE_URL + "/currency")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CacheManager cacheManager;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrencies() {
        return ResponseEntity.ok(currencyService.getCurrencies());
    }

    @Operation(description = "Note: if type does not present then 'SOURCE' will be applied")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addCurrency(@RequestBody CurrencyDto currencyRequest) throws URISyntaxException {
        CurrencyDto currencyDto = currencyService.createCurrency(currencyRequest.getCode(), CurrencyType.BASE);
        return ResponseEntity.created(new URI(Api.BASE_URL + "/currencies")).body(currencyDto);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity deleteCurrency(@PathVariable String code) {
        currencyService.removeCurrency(code);
        return ResponseEntity.ok().build();
    }

}
