package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, UUID> {

}
