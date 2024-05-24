package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.controller.handler.GlobalControllerExceptionHandler;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.domain.entity.payload.ErrorResponse;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyController.class)
@ContextConfiguration(classes = {CurrencyController.class, GlobalControllerExceptionHandler.class})
class CurrencyControllerIT {
    private static final String CURRENCY_REQUEST_URL = Api.BASE_URL + "/currency";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CurrencyService currencyService;

    @Test
    public void getCurrencies_WhenRequested_ThenCurrencyReturnHttpStatusOk() throws Exception {
        CurrencyDto currencyDtoUSDBase = CurrencyDto.builder()
                .setCode("USD")
                .build();
        CurrencyDto currencyDtoEURSource = CurrencyDto.builder()
                .setCode("EUR")
                .build();
        List<CurrencyDto> response = List.of(currencyDtoUSDBase, currencyDtoEURSource);
        when(currencyService.getCurrencies()).thenReturn(response);
        String responseJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void addCurrency_WhenRequestedCurrencyExist_ThenHandleException() throws Exception {
        CurrencyDto requestCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .build();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Currency already exists")
                .statusCode(HttpStatus.BAD_REQUEST)
                .requestedUrl(CURRENCY_REQUEST_URL)
                .build();
        when(currencyService.createCurrency("USD", CurrencyType.BASE))
                .thenThrow(new IllegalArgumentException("Currency already exists"));
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDto);
        String responseString = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(responseString));
    }

    @Test
    public void deleteCurrency_WhenRequestedWithCode_ThenReturnHttpStatusOk() throws Exception {
        String currencyCode = "USD";

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(CURRENCY_REQUEST_URL + "/{code}", currencyCode))
                .andExpect(status().isOk());

        verify(currencyService).removeCurrency(currencyCode);
    }

}
