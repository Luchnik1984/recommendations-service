package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * УПРОЩЕННЫЙ ТЕСТ ДЛЯ ПРОВЕРКИ ОСНОВНОЙ БАЗЫ ТРАНЗАКЦИЙ
 */
@SpringBootTest
@ActiveProfiles("test")
public class SimpleH2Test {

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    void testH2DatabaseWorks() {
        // Простая проверка что база работает
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);
    }

    @Test
    void testTablesCreated() {
        // Проверяем что основные таблицы созданы
        assertTrue(tableExists("USERS"), "Таблица USERS должна существовать");
        assertTrue(tableExists("PRODUCTS"), "Таблица PRODUCTS должна существовать");
        assertTrue(tableExists("TRANSACTIONS"), "Таблица TRANSACTIONS должна существовать");
    }

    @Test
    void testDataLoaded() {
        // Проверяем что тестовые данные загружены
        Integer usersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
        Integer productsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCTS", Integer.class);
        Integer transactionsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);

        assertTrue(usersCount > 0, "Должны быть тестовые пользователи");
        assertTrue(productsCount > 0, "Должны быть тестовые продукты");
        assertTrue(transactionsCount > 0, "Должны быть тестовые транзакции");
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
