package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.dto.CurrencyDto;
import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.domain.entity.enums.CurrencyType;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CustomCacheResolverStub rateCacheResolver;
    @InjectMocks
    private DefaultCurrencyService testee;

    @Test
    public void getCurrencies_WhenCalled_ThenFindAll() {
        testee.getCurrencies();

        verify(currencyRepository).findAll();
    }

    @Test
    public void getCurrencyCodes_WhenCalled_ThenReturnCodesByType() {
        CurrencyType type = CurrencyType.BASE;

        testee.getCurrencyCodes(type);

        verify(currencyRepository).findAll();
    }

    @Test
    public void createCurrency_WhenCalledWithNewCodeAndBaseType_ThenUpdateBaseType() {
        String code = "USD";
        CurrencyType type = CurrencyType.BASE;
        Currency currencyMock = mock(Currency.class);
        UUID uuidMock = mock(UUID.class);
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.empty());
        when(currencyRepository.findTopByType(CurrencyType.BASE)).thenReturn(Optional.of(currencyMock));
        when(currencyMock.getId()).thenReturn(uuidMock);
        when(currencyMock.getCode()).thenReturn(code);

        CurrencyDto expected = testee.createCurrency(code, type);

        assertThat(expected.getCode()).isEqualTo(code);
        assertThat(expected.getType()).isEqualTo(type);

        verify(currencyRepository, times(2)).save(any(Currency.class));
    }

    @Test
    public void createCurrency_WhenCalledWithNewCodeAndSourceType_ThenNotUpdateBaseType() {
        String code = "USD";
        CurrencyType type = CurrencyType.BASE;
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.empty());

        CurrencyDto expected = testee.createCurrency(code, type);

        assertThat(expected.getCode()).isEqualTo(code);
        assertThat(expected.getType()).isEqualTo(type);

        verify(currencyRepository).save(any(Currency.class));
    }

    @Test
    public void createCurrency_WhenCalledWithExistingCodeAndSourceType_ThenReturnNull() {
        String code = "USD";
        CurrencyType type = CurrencyType.BASE;
        UUID uuidMock = mock(UUID.class);
        Currency currency = new Currency(uuidMock, code, type);
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.of(currency));

        CurrencyDto expected = testee.createCurrency(code, type);

        assertThat(expected).isNull();
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    public void updateCurrency_WhenCalledWithExistingCodeAndBaseType_ThenUpdateBaseType() {
        String code = "USD";
        CurrencyType type = CurrencyType.BASE;
        UUID uuidMock = mock(UUID.class);
        Currency currencyWas = new Currency(uuidMock, code, CurrencyType.SOURCE);
        Currency currencyToUpdate = new Currency(uuidMock, code, CurrencyType.BASE);
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.of(currencyWas));
        when(currencyRepository.findTopByType(CurrencyType.BASE)).thenReturn(Optional.of(currencyToUpdate));

        CurrencyDto expected = testee.updateCurrency(code, type);

        assertThat(expected.getCode()).isEqualTo(code);
        assertThat(expected.getType()).isEqualTo(type);

        verify(currencyRepository, times(2)).save(any(Currency.class));
    }

    @Test
    public void updateCurrency_WhenCalledWithExistingCodeAndWrongBaseType_ThenBaseTypeHasNoUpdate() {
        String code = "USD";
        CurrencyType type = CurrencyType.SOURCE;
        UUID uuidMock = mock(UUID.class);
        Currency currencyWas = new Currency(uuidMock, code, CurrencyType.BASE);
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.of(currencyWas));

        CurrencyDto expected = testee.updateCurrency(code, type);

        assertThat(expected.getCode()).isEqualTo(code);
        assertThat(expected.getType()).isEqualTo(CurrencyType.BASE);

        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    public void updateCurrency_WhenCalledWithExistingCodeAndBaseType_ThenReturnExisting() {
        String code = "USD";
        CurrencyType type = CurrencyType.BASE;
        UUID uuidMock = mock(UUID.class);
        Currency currencyWas = new Currency(uuidMock, code, CurrencyType.BASE);
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.of(currencyWas));

        CurrencyDto expected = testee.updateCurrency(code, type);

        assertThat(expected.getCode()).isEqualTo(code);
        assertThat(expected.getType()).isEqualTo(type);

        verifyNoMoreInteractions(currencyRepository);
    }

}
