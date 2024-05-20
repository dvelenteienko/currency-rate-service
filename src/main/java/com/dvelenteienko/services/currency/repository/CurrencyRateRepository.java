package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, UUID> {

    List<CurrencyRate> findAllByBaseCurrencyCodeAndDateBetweenOrderByDateDesc(Currency base, LocalDateTime from, LocalDateTime to);
    List<CurrencyRate> findAllBySourceAndDateBetweenOrderByDateDesc(Currency source, LocalDateTime from, LocalDateTime to);

//    Long deleteByBaseCode(String code);
//    Long deleteAllByBase(List<String> codes);
}
