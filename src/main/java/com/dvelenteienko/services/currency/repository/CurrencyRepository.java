package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findTopByCode(String code);

    Currency getByCode(String code);
    List<Currency> getAllByCodeIn(List<String> codes);

}
