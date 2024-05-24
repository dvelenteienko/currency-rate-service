package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CurrencyRateRepository extends JpaRepository<Rate, UUID> {

    List<Rate> findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc(Currency baseCurrencyCode, LocalDateTime from, LocalDateTime to);

    List<Rate> findAllBySourceAndDateBetweenOrderByDateDesc(String sourceCode, LocalDateTime from, LocalDateTime to);
    void removeAllBySource(String sourceCode);

    boolean existsBySource(String source);

}
