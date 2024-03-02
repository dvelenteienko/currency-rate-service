package com.dvelenteienko.services.currency.domain.entity;

import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@SuperBuilder(setterPrefix = "set")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Currency {

    @Id
    @JsonIgnore
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyType type;
}

