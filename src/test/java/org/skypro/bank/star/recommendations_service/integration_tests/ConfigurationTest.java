package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ПРОСТОЙ ТЕСТ ДЛЯ ПРОВЕРКИ КОНФИГУРАЦИИ H2( in memory) тестовых баз
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConfigurationTest {

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate transactionsJdbcTemplate;

    @Autowired
    private javax.sql.DataSource dataSource; // Spring Boot создаст автоматически

    @Test
    void testTransactionsDatabaseConfiguration() {
        assertNotNull(transactionsJdbcTemplate, "H2 база транзакций должна быть настроена");


        Integer result = transactionsJdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);


        assertTrue(tableExists(transactionsJdbcTemplate, "USERS"));
        assertTrue(tableExists(transactionsJdbcTemplate, "PRODUCTS"));
        assertTrue(tableExists(transactionsJdbcTemplate, "TRANSACTIONS"));

        System.out.println(" H2 Transactions database работает корректно");
    }

    @Test
    void testDynamicRulesDatabaseConfiguration() {
        assertNotNull(dataSource, "Dynamic Rules DataSource должен быть создан");

        // Проверяем что база работает
        JdbcTemplate dynamicJdbcTemplate = new JdbcTemplate(dataSource);
        Integer result = dynamicJdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);

        System.out.println(" Dynamic Rules DataSource создан и работает");


        try {

            Thread.sleep(100);

            boolean tablesExist = tableExists(dynamicJdbcTemplate, "DYNAMIC_RULES") ||
                    tableExists(dynamicJdbcTemplate, "RULE_QUERIES") ||
                    tableExists(dynamicJdbcTemplate, "RULE_QUERY_ARGUMENTS");

            if (tablesExist) {
                System.out.println(" Dynamic rules tables созданы");
            } else {
                System.out.println(" Dynamic rules tables создаются Hibernate...");

            }
        } catch (Exception e) {
            System.out.println(" Проверка таблиц: " + e.getMessage());
        }
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

    @Test
    void testLiquibaseExecution() {

        List<String> tables = transactionsJdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES",
                String.class
        );

        System.out.println("=== TABLES IN TRANSACTIONS DATABASE ===");
        tables.forEach(System.out::println);
        System.out.println("=======================================");

        // Проверим данные в таблицах
        if (tables.contains("USERS")) {
            Integer usersCount = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM USERS", Integer.class);
            System.out.println("Users count: " + usersCount);
        }
    }

    @Test
    void testLiquibaseTransactionsDatabase() {
        System.out.println("=== DIAGNOSTIC: LIQUIBASE TRANSACTIONS DATABASE ===");


        List<String> tables = transactionsJdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
        );

        System.out.println("Tables in transactions database:");
        tables.forEach(table -> System.out.println("  - " + table));


        boolean hasLiquibaseTables = tables.stream()
                .anyMatch(table -> table.contains("CHANGELOG"));
        System.out.println("Liquibase tables created: " + hasLiquibaseTables);


        if (tables.contains("USERS")) {
            Integer usersCount = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM USERS", Integer.class);
            System.out.println("Users count: " + usersCount);


            List<Map<String, Object>> sampleUsers = transactionsJdbcTemplate.queryForList(
                    "SELECT * FROM USERS LIMIT 3");
            System.out.println("Sample users: " + sampleUsers);
        }

        if (tables.contains("PRODUCTS")) {
            Integer productsCount = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM PRODUCTS", Integer.class);
            System.out.println("Products count: " + productsCount);
        }

        if (tables.contains("TRANSACTIONS")) {
            Integer transactionsCount = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);
            System.out.println("Transactions count: " + transactionsCount);
        }

        System.out.println("=== END DIAGNOSTIC ===");


        assertTrue(tables.contains("USERS"), "Таблица USERS должна существовать");
        assertTrue(tables.contains("PRODUCTS"), "Таблица PRODUCTS должна существовать");
        assertTrue(tables.contains("TRANSACTIONS"), "Таблица TRANSACTIONS должна существовать");
    }
}