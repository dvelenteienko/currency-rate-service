package com.dvelenteienko.services.currency.controller;

import com.dvelenteienko.services.currency.config.ClientWebConfig;
import com.dvelenteienko.services.currency.controller.api.Api;
import com.dvelenteienko.services.currency.controller.handler.GlobalControllerExceptionHandler;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.service.CurrencyRateService;
import com.dvelenteienko.services.currency.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {CurrencyRateController.class, CurrencyController.class})
@ContextConfiguration(classes = {CurrencyRateController.class, CurrencyController.class, GlobalControllerExceptionHandler.class})
public abstract class AbstractTestController {

    public static final String BASE_CODE = "USD";
    public static final Currency BASE_CURRENCY = Currency.builder().setCode(BASE_CODE).build();
    public static final String SOURCE_CODE = "EUR";
    public static final Currency SOURCE_CURRENCY = Currency.builder().setCode(SOURCE_CODE).build();
    public static final String CURRENCY_CODE_REQUEST_PARAM = "code";
    public static final String CURRENCY_CODES_REQUEST_PARAM = "currencies";
    public static final String DATE_FROM_REQUEST_PARAM = "dateFrom";
    public static final String DATE_TO_REQUEST_PARAM = "dateTo";
    public static final String RATE_REQUEST_URL = Api.BASE_URL + "/rate";
    public static final String EXCHANGE_CURRENCY_RATES_REQUEST_URL = RATE_REQUEST_URL + "/exchange";
    public static final String CURRENCY_REQUEST_URL = Api.BASE_URL + "/currency";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected CurrencyRateService currencyRateService;
    @MockBean
    protected CurrencyService currencyService;

}
