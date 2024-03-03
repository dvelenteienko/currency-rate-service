package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(CurrencyController.class)
@ContextConfiguration(classes = {CurrencyController.class})
class CurrencyControllerIT {
    private static final String SHOW_TYPE = "showType";
    private static final String CURRENCY_REQUEST_URL = Api.BASE_URL + "/currencies";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CurrencyService currencyService;

    @Test
    public void getCurrencies_WhenRequestedWithShowType_ThenCurrencyReturnWithTypeAndHttpStatusOk() throws Exception {
        CurrencyDto currencyDtoUSDBase = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.BASE)
                .build();
        CurrencyDto currencyDtoEURSource = CurrencyDto.builder()
                .setCode("EUR")
                .setType(CurrencyType.SOURCE)
                .build();
        List<CurrencyDto> response = List.of(currencyDtoUSDBase, currencyDtoEURSource);
        when(currencyService.getCurrencies()).thenReturn(response);
        String responseJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam(SHOW_TYPE, "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void getCurrencies_WhenRequestedWithoutShowType_ThenReturnArrayOfCurrencyCodesAndHttpStatusOk() throws Exception {
        CurrencyDto currencyDtoUSDBase = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.BASE)
                .build();
        CurrencyDto currencyDtoEURSource = CurrencyDto.builder()
                .setCode("EUR")
                .setType(CurrencyType.SOURCE)
                .build();
        List<CurrencyDto> response = List.of(currencyDtoUSDBase, currencyDtoEURSource);
        when(currencyService.getCurrencies()).thenReturn(response);
        String responseString = "[\"EUR\",\"USD\"]";

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(responseString));
    }

    @Test
    public void addCurrency_WhenRequestedWithCodeAndType_ThenCurrencyCreatedAndHttpStatusOk() throws Exception {
        CurrencyDto currencyDtoUSDBase = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.BASE)
                .build();
        when(currencyService.createCurrency("USD", CurrencyType.BASE)).thenReturn(currencyDtoUSDBase);
        String requestJson = objectMapper.writeValueAsString(currencyDtoUSDBase);
        String responseJson = objectMapper.writeValueAsString(currencyDtoUSDBase);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void addCurrency_WhenRequestedWithCodeOnly_ThenCurrencyCreatedWithSourceTypeAndHttpStatusOk() throws Exception {
        CurrencyDto requestCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .build();
        CurrencyDto responseCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.SOURCE)
                .build();
        when(currencyService.createCurrency("USD", CurrencyType.SOURCE)).thenReturn(responseCurrencyDto);
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDto);
        String responseJson = objectMapper.writeValueAsString(responseCurrencyDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void addCurrency_WhenRequestedWithCodeAndTypeThatExist_ThenReturnRequestedCurrencyAndHttpStatusOk() throws Exception {
        CurrencyDto requestCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.SOURCE)
                .build();
        when(currencyService.createCurrency("USD", CurrencyType.SOURCE)).thenReturn(null);
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDto);
        String responseJson = objectMapper.writeValueAsString(requestCurrencyDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void updateCurrency_WhenRequestedWithCodeAndType_ThenCurrencyUpdatedAndHttpStatusOk() throws Exception {
        CurrencyDto currencyDtoUSDBase = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.BASE)
                .build();
        when(currencyService.updateCurrency("USD", CurrencyType.BASE)).thenReturn(currencyDtoUSDBase);
        String requestJson = objectMapper.writeValueAsString(currencyDtoUSDBase);
        String responseJson = objectMapper.writeValueAsString(currencyDtoUSDBase);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void updateCurrency_WhenRequestedWithCodeOnly_ThenCurrencyUpdatedWithSourceTypeAndHttpStatusOk() throws Exception {
        CurrencyDto requestCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .build();
        CurrencyDto responseCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.SOURCE)
                .build();
        when(currencyService.updateCurrency("USD", CurrencyType.SOURCE)).thenReturn(responseCurrencyDto);
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDto);
        String responseJson = objectMapper.writeValueAsString(responseCurrencyDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    public void updateCurrency_WhenRequestedWithCodeAndTypeThatExist_ThenReturnRequestedCurrencyAndHttpStatusOk() throws Exception {
        CurrencyDto requestCurrencyDto = CurrencyDto.builder()
                .setCode("USD")
                .setType(CurrencyType.SOURCE)
                .build();
        when(currencyService.updateCurrency("USD", CurrencyType.SOURCE)).thenReturn(null);
        String requestJson = objectMapper.writeValueAsString(requestCurrencyDto);
        String responseJson = objectMapper.writeValueAsString(requestCurrencyDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(CURRENCY_REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

}
