package com.dvelenteienko.services.currency.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "rates")
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrencyRate {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    private String base;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, scale = 10)
    private Double rate;
}
