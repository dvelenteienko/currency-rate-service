package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CurrencyClientApiConfigProperties;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.entity.payload.CurrencyData;
import com.dvelenteienko.services.currency.domain.entity.payload.CurrencyRateResponse;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultCurrencyExchangeDataService implements CurrencyExchangeDataService {

    private final RestTemplate restTemplate;
    private final CurrencyClientApiConfigProperties currencyClientApiConfigProperties;

    @Override
    public List<CurrencyRateDto> getCurrencyRateDtos(String baseCurrency, Set<String> currencies) {
        List<CurrencyRateDto> currencyRateDtos = new ArrayList<>();
        if (StringUtils.isNotBlank(baseCurrency) && !currencies.isEmpty()) {
            CurrencyRateResponse response = callCurrencyRateApi(baseCurrency, currencies);
            final LocalDate lastUpdatedAt = parseDate(response.meta().lastUpdatedAt());
            currencyRateDtos = response.data().entrySet().stream()
                    .map(e -> new CurrencyRateDto(e.getValue().code(), baseCurrency, lastUpdatedAt, e.getValue().value()))
                    .toList();
        }
        return currencyRateDtos;
    }

    private CurrencyRateResponse callCurrencyRateApi(String baseCurrency, Set<String> currencies) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Connection", "keep-alive");
        final HttpEntity<String> headersEntity = new HttpEntity<>(headers);
        StringBuilder collectUrl = new StringBuilder();
        collectUrl.append(currencyClientApiConfigProperties.getUrl())
                .append("?apikey=")
                .append(currencyClientApiConfigProperties.getApiKey())
                .append("&base_currency=")
                .append(baseCurrency)
                .append("&currencies=")
                .append(String.join(",", currencies));
        String url = collectUrl.toString();
        ResponseEntity<CurrencyRateResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, headersEntity,
                CurrencyRateResponse.class);
        log.info("Getting response with status [{}]", responseEntity.getStatusCode());
        return responseEntity.getBody();
    }

    private LocalDate parseDate(String dateTimeString) {
        Instant instant = Instant.parse(dateTimeString);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toLocalDate();
    }
}
