package com.dvelenteienko.services.currency.repository;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DBRider
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
public class CurrencyRepositoryIT {

    @Autowired
    private CurrencyRepository testee;

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRepository-input.yml")
    void currencyRepository_WhenFindTopByCode_ThenReturnCurrency() {

        Optional<Currency> currency = testee.findTopByCode("EUR");

//        assertThat(currency.get().getType()).isEqualTo(CurrencyType.SOURCE);
    }

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRepository-input.yml")
    void currencyRepository_WhenFindTopByType_ThenReturnCurrency() {

//        Optional<Currency> currency = testee.findTopByType(CurrencyType.SOURCE);

//        assertThat(currency.get().getCode()).isEqualTo("CAD");
    }

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRepository-input.yml")
    void currencyRepository_WhenFindByType_ThenReturnCurrency() {

//        List<Currency> currencies = testee.findByType(CurrencyType.SOURCE);

//        assertThat(currencies)
//                .hasSize(3)
//                .allMatch(c -> c.getType() == CurrencyType.SOURCE);
    }

    @Test
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRepository-input.yml")
    void currencyRepository_WhenGetCodesByType_ThenReturnCurrencyCodes() {

//        Set<String> codes = testee.getCodesByType(CurrencyType.SOURCE);

//        assertThat(codes)
//                .hasSize(3)
//                .contains("CAD", "GBP", "EUR");
    }

    @Test
    @ExpectedDataSet(ignoreCols = {"id"}, orderBy = {"type"}, value = "services/currency/repository/CurrencyRepository-expected.yml")
    @DataSet(cleanBefore = true, value = "services/currency/repository/CurrencyRepository-save-input.yml")
    void currencyRepository_WhenSave_ThenReturnCurrency() {

//        Currency currency = testee.save(new Currency(null, "AUD", CurrencyType.SOURCE, List.of()));

//        assertThat(currency.getCode()).isEqualTo("AUD");
//        assertThat(currency.getType()).isEqualTo(CurrencyType.SOURCE);
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan("com.dvelenteienko.services.currency.domain.entity")
    @EnableJpaRepositories("com.dvelenteienko.services.currency.repository")
    static class TestConfig {

    }
}
