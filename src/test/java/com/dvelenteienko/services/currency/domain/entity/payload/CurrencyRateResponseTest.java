package com.dvelenteienko.services.currency.domain.entity.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyRateResponseTest {

    @Test
    public void deserialize_WhenJsonReceived_ThenValuesMappedCorrectly() throws JsonProcessingException {
        String json = "{\"meta\":{\"last_updated_at\":\"2023-06-23T10:15:59Z\"},\"data\":{\"AED\":{\"code\":\"AED\",\"value\":3.67306}}}";
        String AEDKey = "AED";
        ObjectMapper objectMapper = new ObjectMapper();

        CurrencyRateResponse testee = objectMapper.readValue(json, CurrencyRateResponse.class);
        Meta meta = testee.meta();
        Map<String, CurrencyData> currencyDataByCurrencyCode = testee.data();

        assertThat(meta.lastUpdatedAt()).isEqualTo("2023-06-23T10:15:59Z");
        assertThat(currencyDataByCurrencyCode).containsKey(AEDKey);
        assertThat(currencyDataByCurrencyCode.get(AEDKey).code()).isEqualTo("AED");
        assertThat(currencyDataByCurrencyCode.get(AEDKey).value()).isEqualTo(3.67306);

    }
}
