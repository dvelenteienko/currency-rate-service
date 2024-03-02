package com.dvelenteienko.services.currency.domain.entity.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record Meta(@JsonProperty("last_updated_at") String lastUpdatedAt) {
}
