package org.skypro.bank.star.recommendations_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.bank.star.recommendations_service.configuration.RecommendationRulesConfiguration;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.skypro.bank.star.recommendations_service.rule.Invest500RuleSet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Invest500RuleSetTest {

    @Mock
    private UserDataRepositoryImpl userDataRepository;

    @Mock
    private RecommendationRulesConfiguration rulesConfig;

    @Mock
    private RecommendationRulesConfiguration.RuleConfig ruleConfig;

    @InjectMocks
    private Invest500RuleSet invest500RuleSet;

    @Test
    void testCheckUser_AllRulesPass() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");

        when(rulesConfig.getInvest500Config()).thenReturn(ruleConfig);
        when(ruleConfig.getProductId()).thenReturn(productId);
        when(ruleConfig.getProductName()).thenReturn("Invest 500");
        when(ruleConfig.getProductDescription()).thenReturn("Test description");
        when(ruleConfig.getThreshold()).thenReturn(BigDecimal.valueOf(1000));

        when(userDataRepository.hasProductType(userId, ProductType.DEBIT)).thenReturn(true);
        when(userDataRepository.hasProductType(userId, ProductType.INVEST)).thenReturn(false);
        when(userDataRepository.getTotalAmount(userId, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(new BigDecimal("1500"));

        Optional<RecommendationDTO> result = invest500RuleSet.checkUser(userId);

        assertTrue(result.isPresent());
        assertEquals(productId, result.get().id());
        assertEquals("Invest 500", result.get().name());
    }

    @Test
    void testCheckUser_NoDebitProduct() {
        UUID userId = UUID.randomUUID();

        when(userDataRepository.hasProductType(userId, ProductType.DEBIT)).thenReturn(false);

        Optional<RecommendationDTO> result = invest500RuleSet.checkUser(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void testCheckUser_HasInvestProduct() {
        UUID userId = UUID.randomUUID();

        when(userDataRepository.hasProductType(userId, ProductType.DEBIT)).thenReturn(true);
        when(userDataRepository.hasProductType(userId, ProductType.INVEST)).thenReturn(true);

        Optional<RecommendationDTO> result = invest500RuleSet.checkUser(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void testCheckUser_InsufficientSavingDeposits() {
        UUID userId = UUID.randomUUID();

        when(rulesConfig.getInvest500Config()).thenReturn(ruleConfig);
        when(ruleConfig.getThreshold()).thenReturn(BigDecimal.valueOf(1000));

        when(userDataRepository.hasProductType(userId, ProductType.DEBIT)).thenReturn(true);
        when(userDataRepository.hasProductType(userId, ProductType.INVEST)).thenReturn(false);
        when(userDataRepository.getTotalAmount(userId, ProductType.SAVING, TransactionType.DEPOSIT))
                .thenReturn(new BigDecimal("500"));

        Optional<RecommendationDTO> result = invest500RuleSet.checkUser(userId);

        assertFalse(result.isPresent());
    }
}
