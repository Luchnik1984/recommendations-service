package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ТЕСТ ДЛЯ ПРОВЕРКИ SPRING КОНФИГУРАЦИИ С LIQUIBASE
 * Проверяет, что Liquibase автоматически создает структуру БД
 */
@SpringBootTest
@ActiveProfiles("test")
public class SpringH2IntegrationTest {

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseInitializedByLiquibase() {
        // Проверяем что таблицы созданы через Liquibase
        assertTrue(tableExists("USERS"), "Таблица USERS должна быть создана Liquibase");
        assertTrue(tableExists("PRODUCTS"), "Таблица PRODUCTS должна быть создана Liquibase");
        assertTrue(tableExists("TRANSACTIONS"), "Таблица TRANSACTIONS должна быть создана Liquibase");
    }

    @Test
    void testTestDataLoadedByLiquibase() {
        // Проверяем что тестовые данные загружены
        Integer usersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
        Integer productsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCTS", Integer.class);
        Integer transactionsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);

        assertTrue(usersCount >= 5, "Должны быть загружены тестовые пользователи из Liquibase");
        assertTrue(productsCount >= 8, "Должны быть загружены тестовые продукты из Liquibase");
        assertTrue(transactionsCount >= 15, "Должны быть загружены тестовые транзакции из Liquibase");
    }

    @Test
    void testForeignKeyRelationships() {
        // Проверяем что внешние ключи работают
        List<String> constraints = jdbcTemplate.queryForList(
                "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                        "WHERE TABLE_NAME = 'TRANSACTIONS' AND CONSTRAINT_TYPE = 'FOREIGN KEY'",
                String.class);

        assertFalse(constraints.isEmpty(), "Должны быть внешние ключи в TRANSACTIONS");

        // Проверяем что можно выполнить JOIN запрос (внешние ключи работают)
        Integer joinResult = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TRANSACTIONS t " +
                        "JOIN PRODUCTS p ON t.product_id = p.id " +
                        "JOIN USERS u ON t.user_id = u.id",
                Integer.class);

        assertTrue(joinResult > 0, "JOIN запросы должны работать корректно");
    }

    private boolean tableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
                    Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
