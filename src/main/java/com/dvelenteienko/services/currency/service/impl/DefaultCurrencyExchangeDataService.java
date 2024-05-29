package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CacheConfig;
import com.dvelenteienko.services.currency.config.CurrencyClientApiConfigProperties;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.entity.payload.CurrencyRateResponse;
import com.dvelenteienko.services.currency.service.CurrencyExchangeDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultCurrencyExchangeDataService implements CurrencyExchangeDataService {

    private final RestTemplate restTemplate;
    private final CurrencyClientApiConfigProperties currencyClientApiConfigProperties;

    @Cacheable(value = CacheConfig.RATE_CACHE_NAME, key = "#baseCurrency")
    @Override
    public List<CurrencyRateDTO> getExchangeCurrencyRate(String baseCurrency, List<String> codes) {
        List<CurrencyRateDTO> rates = new ArrayList<>();
        if (StringUtils.isNotBlank(baseCurrency) && !codes.isEmpty()) {
            CurrencyRateResponse response = callCurrencyRateApi(baseCurrency, codes);
            final LocalDateTime lastUpdatedAt = parseDate(response.meta().lastUpdatedAt());
            rates = response.data().values().stream()
                    .map(currencyData -> CurrencyRateDTO.builder()
                            .baseCurrency(baseCurrency)
                            .sourceCurrency(currencyData.code())
                            .date(lastUpdatedAt)
                            .rate(currencyData.value())
                            .build())
                    .collect(Collectors.toList());
        }
        return rates;
    }

    private CurrencyRateResponse callCurrencyRateApi(String baseCurrency, List<String> currencies) {
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

    private LocalDateTime parseDate(String dateTimeString) {
        Instant instant = Instant.parse(dateTimeString);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toLocalDateTime();
    }
}
