package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, UUID> {

    List<CurrencyRate> findAllByBaseAndDateBetweenOrderByDateDesc(String base, LocalDateTime from, LocalDateTime to);
    List<CurrencyRate> findAllByBase(String base);

}
