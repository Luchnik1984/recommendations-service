package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
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
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
    }

    @Override
    public BigDecimal getTotalDepositsAmount(UUID userId, ProductType productType) {
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0) AS total_deposits
                FROM TRANSACTIONS t
                INNER JOIN PRODUCTS p ON t.product_id = p.id
                WHERE t.user_id = ?
                AND p.type = ?
                AND t.type = 'DEPOSIT';
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType.name());
    }

    @Override

    public BigDecimal getTotalWithdrawalsAmount(UUID userId, ProductType productType) {
        String sql = """
                SELECT COALESCE(SUM(t.amount), 0) AS total_withdrawals
                   FROM TRANSACTIONS t
                   INNER JOIN PRODUCTS p ON t.product_id = p.id
                   WHERE t.user_id = ?
                   AND p.type = ?
                   AND t.type = 'WITHDRAW';
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType.name());
    }

    @Override
    public boolean isDepositsGreaterThanWithdrawals(UUID userId, ProductType productType) {
        BigDecimal deposits = getTotalDepositsAmount(userId, productType);
        BigDecimal spends = getTotalWithdrawalsAmount(userId, productType);
        return deposits.compareTo(spends) > 0;
    }

}
