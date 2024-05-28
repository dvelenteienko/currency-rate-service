package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.controller.handler.GlobalControllerExceptionHandler;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDTO;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDTO;
import com.dvelenteienko.services.currency.util.RequestPeriod;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.domain.entity.payload.ErrorResponse;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyRateController.class)
@ContextConfiguration(classes = {CurrencyRateController.class, GlobalControllerExceptionHandler.class})
class CurrencyRateControllerIT {

    private static final String BASE_CODE = "USD";
    private static final String SOURCE_CODE = "EUR";
    private static final String CURRENCY_CODE_REQUEST_PARAM = "code";
    private static final String CURRENCY_CODES_REQUEST_PARAM = "currencies";
    private static final String DATE_FROM_REQUEST_PARAM = "dateFrom";
    private static final String DATE_TO_REQUEST_PARAM = "dateTo";
    private static final String RATE_REQUEST_URL = Api.BASE_URL + "/rate";
    private static final String EXCHANGE_CURRENCY_RATES_REQUEST_URL = RATE_REQUEST_URL + "/exchange";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CurrencyRateService currencyRateService;
    @MockBean
    private CurrencyService currencyService;

    @Test
    public void getCurrencyRates_WhenRequestedWithValidParams_ThenReturnCurrencyRatesAndHttpStatusOk() throws Exception {
        String dateFromStr = "2024-03-01";
        String dateToStr = "2024-03-03";
        LocalDateTime responseDatetime = LocalDateTime.of(2024, 3, 3, 23, 59, 59);
        CurrencyRateDTO currencyRateDto = CurrencyRateDTO.builder()
                .setRate(1.0)
                .setDate(responseDatetime)
                .setBase(BASE_CODE)
                .setSource(SOURCE_CODE)
                .build();
        List<CurrencyRateDTO> response = List.of(currencyRateDto);
        when(currencyRateService.getCurrencyRates(anyString(), any(RequestPeriod.class), any(CurrencyType.class))).thenReturn(List.of(currencyRateDto));
        String responseJson = objectMapper.writeValueAsString(response);

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
    public void exchangeCurrencyRates_WhenRequestedWithValidParams_ThenExchangeCurrencyRatesAndHttpStatusOk() throws Exception {
        CurrencyDTO currencyDTOBase = CurrencyDTO.builder()
                .setCode(BASE_CODE)
                .setType(CurrencyType.BASE)
                .build();
        CurrencyDTO currencyDTOSource = CurrencyDTO.builder()
                .setCode(SOURCE_CODE)
                .setType(CurrencyType.BASE)
                .build();
        List<CurrencyRateDTO> currencyRateDTOS = List.of(CurrencyRateDTO.builder()
                .setRate(1.0)
                .setBase(BASE_CODE)
                .setSource(SOURCE_CODE)
                .setDate(LocalDateTime.of(2024, 5, 1, 23, 59, 59))
                .build());
        List<CurrencyDTO> currencies = List.of(currencyDTOBase, currencyDTOSource);
        when(currencyService.getCurrencies()).thenReturn(currencies);
        when(currencyRateService.getCurrencyRates(anyString(), any(RequestPeriod.class), any(CurrencyType.class)))
                .thenReturn(List.of());
        when(currencyRateService.fetchRates(anyString(), anyList())).thenReturn(currencyRateDTOS);
        String responseJson = objectMapper.writeValueAsString(currencyRateDTOS);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL, BASE_CODE)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void exchangeCurrencyRates_WhenRequestedCurrenciesWithExistingRates_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        CurrencyDTO currencyDTOBase = CurrencyDTO.builder()
                .setCode(BASE_CODE)
                .setType(CurrencyType.BASE)
                .build();
        CurrencyDTO currencyDTOSource = CurrencyDTO.builder()
                .setCode(SOURCE_CODE)
                .setType(CurrencyType.BASE)
                .build();
        List<CurrencyRateDTO> currencyRateDTOS = List.of(CurrencyRateDTO.builder()
                .setRate(1.0)
                .setBase(BASE_CODE)
                .setSource(SOURCE_CODE)
                .setDate(LocalDateTime.of(2024, 5, 1, 23, 59, 59))
                .build());
        List<CurrencyDTO> currencies = List.of(currencyDTOBase, currencyDTOSource);
        when(currencyService.getCurrencies()).thenReturn(currencies);
        when(currencyRateService.getCurrencyRates(anyString(), any(RequestPeriod.class), any(CurrencyType.class)))
                .thenReturn(currencyRateDTOS);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EXCHANGE_CURRENCY_RATES_REQUEST_URL, BASE_CODE)
                        .queryParam(CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(CURRENCY_CODES_REQUEST_PARAM, SOURCE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("already exist")))
                .andExpect(jsonPath("$.message").value(Matchers.stringContainsInOrder("EUR", "USD")));
    }
}
