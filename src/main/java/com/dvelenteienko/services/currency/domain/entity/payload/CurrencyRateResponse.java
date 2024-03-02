package com.dvelenteienko.services.currency.domain.entity.payload;

import java.util.Map;

public record CurrencyRateResponse(Meta meta, Map<String, CurrencyData> data) {
}
