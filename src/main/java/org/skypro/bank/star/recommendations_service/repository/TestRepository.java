package org.skypro.bank.star.recommendations_service.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository {
    private final JdbcTemplate jdbcTemplate;

    public TestRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getUsersCount() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM USERS",
                Integer.class);
        return result != null ? result : 0;
    }

    public int getProductsCount() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PRODUCTS",
                Integer.class);
        return result != null ? result : 0;
    }

    public int getTransactionsCount() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TRANSACTIONS",
                Integer.class);
        return result != null ? result : 0;
    }
}