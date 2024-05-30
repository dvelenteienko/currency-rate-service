package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrencies() {
        return ResponseEntity.ok(CurrencyMapper.INSTANCE.currenciesToCurrencyDtos(currencyService.getCurrencies()));
    }

    @Operation(description = "Note: if type does not present then 'SOURCE' will be applied")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addCurrency(@RequestBody CurrencyDTO currencyRequest) throws URISyntaxException {
        Currency currency = currencyService.createCurrency(currencyRequest.getCode());
        return ResponseEntity.created(new URI(Api.BASE_URL + "/currencies"))
                .body(CurrencyMapper.INSTANCE.currencyToCurrencyDTO(currency));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity removeCurrency(@PathVariable String code) {
        Currency currency = currencyService.getCurrencies().stream()
                .filter(c -> code.equals(c.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("The currency with code [%s] does not exist", code)));
        currencyService.removeCurrency(currency);
        return ResponseEntity.ok().build();
    }

}
