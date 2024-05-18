package com.dvelenteienko.services.currency.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "rate_rel")
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "source_code", nullable = false)
    private String source;

    @ManyToOne
    @JoinColumn(name = "base_code", referencedColumnName = "code", nullable = false)
    private Currency baseCurrencyCode;

    @Column(nullable = false)
    private LocalDateTime date;
    @Column(nullable = false, scale = 10)
    private Double rate;
}
