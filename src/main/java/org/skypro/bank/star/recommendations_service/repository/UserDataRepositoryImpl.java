package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Реализация репозитория для работы с финансовыми данными пользователей.
 * Содержит оптимизированные SQL запросы к базе данных
 */

@Repository
public class UserDataRepositoryImpl implements UserDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserDataRepositoryImpl(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean hasProductType(UUID userId, ProductType productType) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM TRANSACTIONS t
                    INNER JOIN PRODUCTS p ON t.product_id = p.id
                    WHERE t.user_id = ?
                    AND p.type = ?
                ) AS has_debit_transactions;
                """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType.name());
    }

    @Override
    public BigDecimal getTotalAmount(
            UUID userId, ProductType productType, TransactionType transactionType) {
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0) AS total_deposits
                FROM TRANSACTIONS t
                INNER JOIN PRODUCTS p ON t.product_id = p.id
                WHERE t.user_id = ?
                AND p.type = ?
                AND t.type = ?;
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType.name(), transactionType.name());
    }


    @Override
    public boolean isDepositsGreaterThanWithdrawals(
            UUID userId, ProductType productType) {

        BigDecimal deposits = getTotalAmount(userId, productType, TransactionType.DEPOSIT);
        BigDecimal spends = getTotalAmount(userId, productType, TransactionType.WITHDRAW);
        return deposits.compareTo(spends) > 0;
    }

    @Override
    public int getTransactionCountByProductType(UUID userId, ProductType productType) {
        String sql = """
            SELECT COUNT(*) AS transaction_count
            FROM TRANSACTIONS t
            INNER JOIN PRODUCTS p ON t.product_id = p.id
            WHERE t.user_id = ?
            AND p.type = ?;
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, productType.name());
    }

    public double getTransactionSumByType(UUID userId, ProductType productType, TransactionType transactionType) {
        BigDecimal totalAmount = getTotalAmount(userId, productType, transactionType);
        return totalAmount.doubleValue();
    }
}
