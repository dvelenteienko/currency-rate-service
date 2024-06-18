package com.dvelenteienko.services.currency.service.impl;

import com.dvelenteienko.services.currency.domain.entity.Currency;
import com.dvelenteienko.services.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
class DefaultCurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @InjectMocks
    private DefaultCurrencyService testee;

    @Test
    public void getCurrencies_WhenCalled_ThenFindAll() {
        testee.getCurrencies();

        verify(currencyRepository).findAll();
    }

    @Test
    public void getCurrencyCodes_WhenCalled_ThenReturnCodesByType() {
//        CurrencyType type = CurrencyType.BASE;

        testee.getCurrencies();

        verify(currencyRepository).findAll();
    }

    @Test
    public void createCurrency_WhenCalledWithNewCodeAndBaseType_ThenUpdateBaseType() {
        String code = "USD";
//        CurrencyType type = CurrencyType.BASE;
        Currency currencyMock = mock(Currency.class);
        UUID uuidMock = mock(UUID.class);
//        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.empty());
//        when(currencyRepository.findTopByType(CurrencyType.BASE)).thenReturn(Optional.of(currencyMock));
        when(currencyMock.getId()).thenReturn(uuidMock);
        when(currencyMock.getCode()).thenReturn(code);

//        CurrencyDTO expected = testee.createCurrency(code, type);

//        assertThat(expected.getCode()).isEqualTo(code);
//        assertThat(expected.getType()).isEqualTo(type);

        verify(currencyRepository, times(2)).save(any(Currency.class));
    }

    @Test
    public void createCurrency_WhenCalledWithNewCodeAndSourceType_ThenNotUpdateBaseType() {
        String code = "USD";
//        CurrencyType type = CurrencyType.BASE;
        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.empty());

//        CurrencyDTO expected = testee.createCurrency(code, type);

//        assertThat(expected.getCode()).isEqualTo(code);
//        assertThat(expected.getType()).isEqualTo(type);

        verify(currencyRepository).save(any(Currency.class));
    }

    @Test
    public void createCurrency_WhenCalledWithExistingCodeAndSourceType_ThenReturnNull() {
        String code = "USD";
//        CurrencyType type = CurrencyType.BASE;
        UUID uuidMock = mock(UUID.class);
//        Currency currency = new Currency(uuidMock, code, type, List.of());
//        when(currencyRepository.findTopByCode(code)).thenReturn(Optional.of(currency));

//        CurrencyDTO expected = testee.createCurrency(code, type);

//        assertThat(expected).isNull();
        verifyNoMoreInteractions(currencyRepository);
    }

}
