package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findTopByCode(String code);
    Currency getByCode(String code);
    Optional<Currency> findTopByType(CurrencyType type);

    List<Currency> findByType(CurrencyType type);

    default Set<String> getCodesByType(CurrencyType type) {
        return findByType(type).stream()
                .map(Currency::getCode)
                .collect(Collectors.toSet());
    }

}
