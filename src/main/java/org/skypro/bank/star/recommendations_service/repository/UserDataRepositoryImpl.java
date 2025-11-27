package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.mapper.UserMapper;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Реализация репозитория для работы с финансовыми данными пользователей.
 * Содержит оптимизированные SQL запросы к базе данных
 */

@Repository
@Qualifier("UserDataRepositoryImpl")
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


    @Override
    public List<UserInfoDto> findActiveUsersByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String sql = """
        SELECT DISTINCT
            u.ID,
            u.USERNAME,
            u.FIRST_NAME,
            u.LAST_NAME
        FROM USERS u
        INNER JOIN TRANSACTIONS t ON u.ID = t.USER_ID
        WHERE u.USERNAME = ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UUID id = UUID.fromString(rs.getString("ID"));
            String foundUsername = rs.getString("USERNAME");
            String firstName = rs.getString("FIRST_NAME");
            String lastName = rs.getString("LAST_NAME");

            return UserMapper.toUserInfoDto(id, foundUsername, firstName, lastName);
        }, username.trim());
    }
}
