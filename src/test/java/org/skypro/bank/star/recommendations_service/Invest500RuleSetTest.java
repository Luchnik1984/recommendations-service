package org.skypro.bank.star.recommendations_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.skypro.bank.star.recommendations_service.rule.Invest500RuleSet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Invest500RuleSetTest {

    @Mock
    private UserDataRepositoryImpl userDataRepository;

    @InjectMocks
    private Invest500RuleSet invest500RuleSet;

    @Test
    void testCheckUser_AllRulesPass() {
        UUID userId = UUID.randomUUID();

        when(userDataRepository.hasProductType(userId, ProductType.DEBIT)).thenReturn(true);
        when(userDataRepository.hasProductType(userId, ProductType.INVEST)).thenReturn(false);
        when(userDataRepository.getTotalDepositsAmount(userId, ProductType.SAVING))
                .thenReturn(new BigDecimal("1500"));

        Optional<?> result = invest500RuleSet.checkUser(userId);

        assertTrue(result.isPresent());
    }
}

