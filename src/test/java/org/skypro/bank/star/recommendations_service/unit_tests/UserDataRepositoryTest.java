package org.skypro.bank.star.recommendations_service.unit_tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepositoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserDataRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserDataRepositoryImpl userDataRepository;
    private final BigDecimal totalDepositAmount = BigDecimal.valueOf(1_500);

    @Test
    void testHasProductType() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), any(), any()))
                .thenReturn(true);

        boolean result = userDataRepository.hasProductType(userId, ProductType.DEBIT);

        assertTrue(result);
    }

    @Test
    void testGetTotalAmount_Deposit() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class), any(), any(),any()))
                .thenReturn(totalDepositAmount);

        BigDecimal result = userDataRepository.getTotalAmount(
                userId,
                ProductType.SAVING,
                TransactionType.DEPOSIT);

        assertEquals(totalDepositAmount, result);
    }

    @Test
    void testGetTotalAmount_Withdraw() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class), any(), any(),any()))
                .thenReturn(totalDepositAmount);

        BigDecimal result = userDataRepository.getTotalAmount(
                userId,
                ProductType.SAVING,
                TransactionType.WITHDRAW);

        assertEquals(totalDepositAmount, result);
    }

    @Test
    void testIsDepositsGreaterThanWithdrawals_True() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(2000))  // deposits
                .thenReturn(BigDecimal.valueOf(1000)); // withdrawals

        boolean result = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        assertTrue(result);
    }

    @Test
    void testIsDepositsGreaterThanWithdrawals_False() {
        UUID userId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(BigDecimal.class), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(500))   // deposits
                .thenReturn(BigDecimal.valueOf(1000)); // withdrawals

        boolean result = userDataRepository.isDepositsGreaterThanWithdrawals(userId, ProductType.DEBIT);

        assertFalse(result);
    }
}
