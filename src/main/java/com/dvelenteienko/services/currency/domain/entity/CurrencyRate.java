package com.dvelenteienko.services.currency.domain.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "rates")
@SuperBuilder(setterPrefix = "set")
@EqualsAndHashCode(of = "id")
public class CurrencyRate {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    private String target;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, scale = 10)
    private Double rate;
}
