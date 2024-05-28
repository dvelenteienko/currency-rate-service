package com.dvelenteienko.services.currency.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "rate")
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_currency_id")
    private Currency base;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "source_currency_id")
    private Currency source;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, scale = 10)
    private Double rate;
}
