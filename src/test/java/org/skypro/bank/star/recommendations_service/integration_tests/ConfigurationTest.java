package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ПРОСТОЙ ТЕСТ ДЛЯ ПРОВЕРКИ КОНФИГУРАЦИИ
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConfigurationTest {

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate transactionsJdbcTemplate;

    @Autowired
    private javax.sql.DataSource dynamicRulesDataSource;

    @Test
    void testTransactionsDatabaseConfiguration() {
        assertNotNull(transactionsJdbcTemplate, "H2 база транзакций должна быть настроена");

        // Проверяем что база работает
        Integer result = transactionsJdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);

        // Проверяем что таблицы созданы
        assertTrue(tableExists(transactionsJdbcTemplate, "USERS"));
        assertTrue(tableExists(transactionsJdbcTemplate, "PRODUCTS"));
        assertTrue(tableExists(transactionsJdbcTemplate, "TRANSACTIONS"));

        System.out.println("✅ H2 Transactions database работает корректно");
    }

    @Test
    void testDynamicRulesDatabaseConfiguration() {
        assertNotNull(dynamicRulesDataSource, "Dynamic Rules DataSource должен быть создан");

        // Проверяем что база работает
        JdbcTemplate dynamicJdbcTemplate = new JdbcTemplate(dynamicRulesDataSource);
        Integer result = dynamicJdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);

        System.out.println("✅ Dynamic Rules DataSource создан и работает");
    }

    private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
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