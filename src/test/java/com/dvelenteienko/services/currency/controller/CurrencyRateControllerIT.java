package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyRateDto;
import com.dvelenteienko.services.currency.domain.dto.RequestPeriodDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyRateController.class)
@ContextConfiguration(classes = {CurrencyRateController.class})
class CurrencyRateControllerIT {

    private static final String BASE_CODE = "USD";
    private static final String SOURCE_CODE = "EUR";
    private static final String BASE_CURRENCY_CODE_REQUEST_PARAM = "baseCurrencyCode";
    private static final String DATE_FROM_REQUEST_PARAM = "dateFrom";
    private static final String DATE_TO_REQUEST_PARAM = "dateTo";
    private static final String RATE_REQUEST_URL = Api.BASE_URL + "/rates";
    private static final String RATES_EXCHANGE_REQUEST_URL = Api.BASE_URL + "/rates/exchange/{code}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CurrencyService currencyService;
    @MockBean
    private CurrencyRateService currencyRateService;

    @Test
    public void getCurrencyRates_WhenRequestedWithValidParams_ThenReturnCurrencyRatesAndHttpStatusOk() throws Exception {
        String dateFromStr = "2024-03-01";
        String dateToStr = "2024-03-03";
        LocalDateTime responseDatetime = LocalDateTime.of(2024, 3, 3, 23, 59, 59);
        CurrencyRateDto currencyRateDto = CurrencyRateDto.builder()
                .setRate(0.1)
                .setDate(responseDatetime)
                .setBase(BASE_CODE)
                .setSource(SOURCE_CODE)
                .build();
        List<CurrencyRateDto> response = List.of(currencyRateDto);
        when(currencyRateService.getCurrencyRates(anyString(), any(RequestPeriodDto.class))).thenReturn(List.of(currencyRateDto));
        String responseJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RATE_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(BASE_CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(DATE_FROM_REQUEST_PARAM, dateFromStr)
                        .queryParam(DATE_TO_REQUEST_PARAM, dateToStr))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void getCurrencyRates_WhenRequestedWithDateFromAndDateToNotValid_ThenReturnErrorMessageAndHttpStatusBadRequest() throws Exception {
        String dateFromStr = "2024-03-03";
        String dateToStr = "2024-03-01";

        String responseString = "Date range is not valid. From: [" + dateFromStr + "] To:[" + dateToStr + "]";

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RATE_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(BASE_CURRENCY_CODE_REQUEST_PARAM, BASE_CODE)
                        .queryParam(DATE_FROM_REQUEST_PARAM, dateFromStr)
                        .queryParam(DATE_TO_REQUEST_PARAM, dateToStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(responseString));
    }

    @Test
    public void triggerCurrencyRatesExchange_WhenRequestedWithCurrencyCode_ThenExchangeCurrencyRatesAndHttpStatusOk() throws Exception {
        Set<String> baseCodes = Set.of(BASE_CODE);
        Set<String> sourceCodes = Set.of(SOURCE_CODE);
        when(currencyService.getCurrencyCodes(CurrencyType.BASE)).thenReturn(baseCodes);
        when(currencyService.getCurrencyCodes(CurrencyType.SOURCE)).thenReturn(sourceCodes);
        LocalDateTime responseDatetime = LocalDateTime.of(2024, 3, 3, 23, 59, 59);
        CurrencyRateDto currencyRateDto = CurrencyRateDto.builder()
                .setRate(0.1)
                .setDate(responseDatetime)
                .setBase(BASE_CODE)
                .setSource(SOURCE_CODE)
                .build();
        List<CurrencyRateDto> response = List.of(currencyRateDto);
        when(currencyRateService.createCurrencyRate(BASE_CODE, sourceCodes)).thenReturn(response);
        String responseJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RATES_EXCHANGE_REQUEST_URL, BASE_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void triggerCurrencyRatesExchange_WhenRequestedWithNotBaseCurrencyCode_ThenReturnMessageAndHttpStatusBadRequest() throws Exception {
        Set<String> baseCodes = Set.of(BASE_CODE);
        Set<String> sourceCodes = Set.of(SOURCE_CODE);
        when(currencyService.getCurrencyCodes(CurrencyType.BASE)).thenReturn(baseCodes);
        String responseString = "The currency code " + SOURCE_CODE + " is not a BASE currency";

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RATES_EXCHANGE_REQUEST_URL, "EUR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(responseString));
    }
}
