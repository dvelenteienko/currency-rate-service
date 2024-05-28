package com.dvelenteienko.services.currency.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateDTO {

    @JsonIgnore
    private UUID id;
    private String baseCurrency;
    private String sourceCurrency;
    private LocalDateTime date;
    private Double rate;

}
