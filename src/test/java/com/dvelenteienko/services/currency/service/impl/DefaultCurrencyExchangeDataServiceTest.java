package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.config.CurrencyClientApiConfigProperties;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.entity.payload.CurrencyData;
import com.dvelenteienko.services.currency.domain.entity.payload.CurrencyRateResponse;
import com.dvelenteienko.services.currency.domain.entity.payload.Meta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyExchangeDataServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CurrencyClientApiConfigProperties currencyClientApiConfigProperties;
    @InjectMocks
    private DefaultCurrencyExchangeDataService testee;

    @Test
    public void getExchangeCurrencyRate_WhenAllPropertiesSet_ThenReturnResponse() {
        String baseCurrency = "USD";
        String currencyCode = "EUR";
        double rate = 0.1;
        LocalDateTime localDateTime = LocalDateTime.of(2024, 2, 28, 23, 59, 59);
        Meta meta = new Meta("2024-02-28T23:59:59Z");
        CurrencyData currencyData = new CurrencyData(currencyCode, rate);
        Map<String, CurrencyData> currencyDataMap = Map.of(currencyCode, currencyData);
        CurrencyRateResponse response = new CurrencyRateResponse(meta, currencyDataMap);
        when(currencyClientApiConfigProperties.getUrl()).thenReturn("https://exchangecurrencytest.com");
        when(currencyClientApiConfigProperties.getApiKey()).thenReturn("apikey");
        ResponseEntity<CurrencyRateResponse> responseEntityMock = ResponseEntity.ok().body(response);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(CurrencyRateResponse.class)))
                .thenReturn(responseEntityMock);

        List<CurrencyRateDto> expected = testee.getExchangeCurrencyRate(baseCurrency, List.of(currencyCode));

        assertThat(expected).isNotNull();
        assertThat(expected.get(0).getSource()).isEqualTo(currencyCode);
        assertThat(expected.get(0).getBase()).isEqualTo(baseCurrency);
        assertThat(expected.get(0).getRate()).isEqualTo(rate);
        assertThat(expected.get(0).getDate()).isEqualTo(localDateTime);
    }

}
