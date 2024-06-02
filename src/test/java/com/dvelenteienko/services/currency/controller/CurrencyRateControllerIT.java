package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import com.dvelenteienko.services.currency.domain.entity.payload.ErrorResponse;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import com.dvelenteienko.services.currency.util.RequestPeriod;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CurrencyRateControllerIT extends AbstractTestController {


    @Test
    public void getCurrencyRates_WhenRequestedWithValidParams_ThenReturnCurrencyRatesAndHttpStatusOk() throws Exception {
        String dateFromStr = "2024-03-01";
        String dateToStr = "2024-03-03";
        LocalDateTime responseDatetime = LocalDateTime.of(2024, 3, 3, 23, 59, 59);
        Rate currencyRate = Rate.builder()
                .setRate(1.0)
                .setDate(responseDatetime)
                .setBase(Currency.builder().setCode(BASE_CODE).build())
                .setSource(Currency.builder().setCode(SOURCE_CODE).build())
                .build();
        List<Rate> response = List.of(currencyRate);
        when(currencyRateService.getRates(anyString(), anyList(), any(RequestPeriod.class))).thenReturn(List.of(currencyRate));
        String responseJson = objectMapper.writeValueAsString(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(response));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RATE_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(DATE_FROM_REQUEST_PARAM, dateFromStr)
                        .queryParam(DATE_TO_REQUEST_PARAM, dateToStr))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void getCurrencyRates_WhenRequestedWithDateFromAndDateToNotValid_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        String dateFromStr = "2024-03-03";
        String dateToStr = "2024-03-01";

        String errorMessage = String.format("Date range is not valid. From: [%s] To:[%s]", dateFromStr, dateToStr);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorMessage)
                .statusCode(HttpStatus.BAD_REQUEST)
                .requestedUrl(RATE_REQUEST_URL)
                .build();

        String responseJson = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RATE_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(DATE_FROM_REQUEST_PARAM, dateFromStr)
                        .queryParam(DATE_TO_REQUEST_PARAM, dateToStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(responseJson));
    }

    @Test
    public void exchangeCurrencyRates_WhenRequestedWithSameBaseAndSourceCode_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        String errorMessage = String.format("Cannot get rates of itself code [%s]", SOURCE_CODE);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorMessage)
                .statusCode(HttpStatus.BAD_REQUEST)
                .requestedUrl(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                .build();

        String responseJson = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, SOURCE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void exchangeCurrencyRates_WhenRequestedWithInvalidCurrency_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        String wrongCurrency = "USSD";
        String errorMessage = String.format("Currency [%s] does not exist", wrongCurrency);
        Currency baseCurrency = Currency.builder().setCode(BASE_CODE).build();
        Currency sourceCurrency = Currency.builder().setCode(SOURCE_CODE).build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorMessage)
                .statusCode(HttpStatus.NOT_FOUND)
                .requestedUrl(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                .build();
        when(currencyService.getCurrencies()).thenReturn(List.of(baseCurrency, sourceCurrency));

        String responseJson = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE, wrongCurrency)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void exchangeCurrencyRates_WhenRequestedWithValidParams_ThenExchangeCurrencyRatesAndHttpStatusOk() throws Exception {
        List<Rate> rates = List.of(Rate.builder()
                .setRate(1.0)
                .setBase(BASE_CURRENCY)
                .setSource(SOURCE_CURRENCY)
                .setDate(LocalDateTime.of(2024, 5, 1, 23, 59, 59))
                .build());
        when(currencyService.getCurrencies()).thenReturn(List.of(BASE_CURRENCY, SOURCE_CURRENCY));
        when(currencyRateService.getRates(anyString(), anyList(), any(RequestPeriod.class)))
                .thenReturn(List.of());
        when(currencyRateService.persisRates(anyString(), anyList(), any(RequestPeriod.class))).thenReturn(rates);
        String responseJson = objectMapper.writeValueAsString(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void exchangeCurrencyRates_WhenRequestedCurrenciesWithExistingRates_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        List<Rate> rates = List.of(Rate.builder()
                .setRate(1.0)
                .setBase(BASE_CURRENCY)
                .setSource(SOURCE_CURRENCY)
                .setDate(LocalDateTime.of(2024, 5, 1, 23, 59, 59))
                .build());
        when(currencyService.getCurrencies()).thenReturn(List.of(BASE_CURRENCY, SOURCE_CURRENCY));
        when(currencyRateService.persisRates(anyString(), anyList(), any(RequestPeriod.class))).thenReturn(rates);
        String responseJson = objectMapper.writeValueAsString(CurrencyMapper.INSTANCE.ratesToCurrencyRatesDTOs(rates));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

}
