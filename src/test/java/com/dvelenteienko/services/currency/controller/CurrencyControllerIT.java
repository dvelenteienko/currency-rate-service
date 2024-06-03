package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDTO;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.payload.ErrorResponse;
import com.dvelenteienko.services.currency.domain.mapper.CurrencyMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CurrencyControllerIT extends AbstractTestController {

    @Test
    public void getCurrencies_WhenRequested_ThenCurrencyReturnHttpStatusOk() throws Exception {
        Currency currencyUSDBase = Currency.builder()
                .setCode("USD")
                .build();
        Currency currencyEURSource = Currency.builder()
                .setCode("EUR")
                .build();
        List<Currency> response = List.of(currencyUSDBase, currencyEURSource);
        when(currencyService.getCurrencies()).thenReturn(response);
        String responseJson = objectMapper.writeValueAsString(CurrencyMapper.INSTANCE.currenciesToCurrencyDtos(response));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void addCurrency_WhenRequestedCurrencyExist_ThenHandleException() throws Exception {
        CurrencyDTO requestCurrencyDTO = CurrencyDTO.builder()
                .code("USD")
                .build();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Currency already exists")
                .statusCode(HttpStatus.BAD_REQUEST)
                .requestedUrl(CURRENCY_REQUEST_URL)
                .build();
        when(currencyService.createCurrency("USD"))
                .thenThrow(new IllegalArgumentException("Currency already exists"));
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDTO);
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
        Currency currencyToRemove = Currency.builder()
                .setCode(currencyCode)
                .setId(UUID.randomUUID())
                .build();

        when(currencyService.getCurrencies()).thenReturn(List.of(currencyToRemove));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(CURRENCY_REQUEST_URL + "/{code}", currencyCode))
                .andExpect(status().isOk());

        verify(currencyService).removeCurrency(currencyToRemove);
    }

    @Test
    public void deleteCurrency_WhenCurrencyCodeIsBlank_ThenReturnHttpStatusBadRequest() throws Exception {
        String currencyCode = " ";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("code must not be blank")
                .statusCode(HttpStatus.BAD_REQUEST)
                .requestedUrl(CURRENCY_REQUEST_URL + "/%20")
                .build();
        String responseString = objectMapper.writeValueAsString(errorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(CURRENCY_REQUEST_URL + "/{code}", currencyCode))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(responseString));
    }

}
