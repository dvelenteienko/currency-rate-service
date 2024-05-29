package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CurrencyRateRepository extends JpaRepository<Rate, UUID> {

//    List<Rate> findAllByBaseAndDateBetweenOrderByDateDesc(Currency baseCurrency, LocalDateTime from, LocalDateTime to);

//    List<Rate> findAllBySourceAndDateBetweenOrderByDateDesc(Currency sourceCurrency, LocalDateTime from, LocalDateTime to);
//    List<Rate> findAllByBaseAndSourceInAndDateBetweenOrderByDateDesc(Currency sourceCurrency,
//                                                                     List<Currency> sourceCurrencies,
//                                                                     LocalDateTime from, LocalDateTime to);

    List<Rate> findAllByDateBetweenOrderByDateDesc(LocalDateTime from, LocalDateTime to);

//    boolean existsBySource(Currency sourceCurrency);

}
