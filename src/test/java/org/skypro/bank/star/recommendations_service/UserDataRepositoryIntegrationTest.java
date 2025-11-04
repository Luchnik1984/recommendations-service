package org.skypro.bank.star.recommendations_service;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")

class UserDataRepositoryIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(UserDataRepositoryIntegrationTest.class);

    @Autowired
    private UserDataRepository userDataRepository;

    private final UUID USER_WITH_DEBIT = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final UUID USER_WITH_CREDIT = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID USER_WITH_SAVING = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final UUID NON_EXISTENT_USER = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Test
    void testHasProductType_Debit_Exist() {
        logger.info("Testing hasProductType for DEBIT - should return true");
        boolean result = userDataRepository.hasProductType(USER_WITH_DEBIT, ProductType.DEBIT);
        assertTrue(result, "User should have DEBIT product");
    }

    @Test
    void testHasProductType_NonExistentUser() {
        logger.info("Testing hasProductType for non-existent user");

        boolean result = userDataRepository.hasProductType(NON_EXISTENT_USER, ProductType.DEBIT);

        assertFalse(result, "Non-existent user should not have any products");
    }

    @Test
    void testGetTotalAmount_Deposit() {
        logger.info("Testing getTotalAmount for DEPOSIT transactions");

        BigDecimal result = userDataRepository.getTotalAmount(
                USER_WITH_DEBIT,
                ProductType.DEBIT,
                TransactionType.DEPOSIT
        );

        assertEquals(new BigDecimal("2999"), result, "Total deposits should be 2999");
    }

    @Test
    void testGetTotalAmount_Withdraw() {
        logger.info("Testing getTotalAmount for WITHDRAW transactions");

        BigDecimal result = userDataRepository.getTotalAmount(
                USER_WITH_SAVING,
                ProductType.SAVING,
                TransactionType.WITHDRAW
        );

        assertEquals(new BigDecimal("1999"), result, "Total withdrawals should be 1999");
    }

    @Test
    void testGetTotalAmount_NonExistentCombination() {
        logger.info("Testing getTotalAmount for non-existent combination");

        BigDecimal result = userDataRepository.getTotalAmount(
                USER_WITH_DEBIT,
                ProductType.CREDIT,
                TransactionType.DEPOSIT
        );

        assertEquals(BigDecimal.ZERO, result, "Should return 0 for non-existent combination");
    }

    @Test
    void testIsDepositsGreaterThanWithdrawals_Debit() {
        logger.info("Testing isDepositsGreaterThanWithdrawals for DEBIT");

        boolean result = userDataRepository.isDepositsGreaterThanWithdrawals(USER_WITH_DEBIT, ProductType.DEBIT);

        assertTrue(result, "Deposits should be greater than withdrawals for DEBIT");
    }
}
