package com.dvelenteienko.services.currency.domain.entity;

import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "currency_rel")
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType type;

    @OneToMany(mappedBy = "baseCurrencyCode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CurrencyRate> rates;
}

