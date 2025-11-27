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
 * ПРОСТОЙ ДИАГНОСТИЧЕСКИЙ ТЕСТ.
 * Читает информацию из тестовых баз
 */
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseDiagnosticsTest {

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate transactionsJdbcTemplate;

    @Test
    void simpleDatabaseDiagnostics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" SIMPLE DATABASE DIAGNOSTICS");
        System.out.println("=".repeat(60));

        checkTransactionsDatabase();
        checkDynamicRulesDatabase();

        System.out.println("=".repeat(60));
        System.out.println("✅ DIAGNOSTICS COMPLETED");
        System.out.println("=".repeat(60));
    }

    private void checkTransactionsDatabase() {
        System.out.println("\n TRANSACTIONS DATABASE");
        System.out.println("-".repeat(30));

        try {

            Integer result = transactionsJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertEquals(1, result);
            System.out.println("✅ Database connection: OK");


            List<String> tables = transactionsJdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME",
                    String.class
            );

            System.out.println(" Tables (" + tables.size() + "):");
            tables.forEach(table -> System.out.println("   └─ " + table));


            printTableInfo("USERS", " Users");
            printTableInfo("PRODUCTS", " Products");
            printTableInfo("TRANSACTIONS", " Transactions");

        } catch (Exception e) {
            System.out.println(" Transactions database error: " + e.getMessage());
        }
    }

    private void checkDynamicRulesDatabase() {
        System.out.println("\n DYNAMIC RULES DATABASE");
        System.out.println("-".repeat(30));

        try {
            /* Используем transactions JdbcTemplate для проверки dynamic rules таблиц,
            они создаются в той же H2 in-memory базе Hibernate'ом*/

            boolean hasDynamicRules = tableExists("DYNAMIC_RULES");
            boolean hasRuleQueries = tableExists("RULE_QUERIES");
            boolean hasRuleArgs = tableExists("RULE_QUERY_ARGUMENTS");

            System.out.println(" JPA Tables Status:");
            System.out.println("   └─ DYNAMIC_RULES: " + (hasDynamicRules ? " EXISTS" : " CREATING..."));
            System.out.println("   └─ RULE_QUERIES: " + (hasRuleQueries ? " EXISTS" : " CREATING..."));
            System.out.println("   └─ RULE_QUERY_ARGUMENTS: " + (hasRuleArgs ? " EXISTS" : " CREATING..."));

            if (hasDynamicRules) {
                printTableInfo("DYNAMIC_RULES", " Dynamic Rules");
            }
            if (hasRuleQueries) {
                printTableInfo("RULE_QUERIES", " Rule Queries");
            }

        } catch (Exception e) {
            System.out.println(" Dynamic rules check error: " + e.getMessage());
        }
    }

    private void printTableInfo(String tableName, String title) {
        try {
            if (!tableExists(tableName)) {
                System.out.println(title + ": ️ Table not found");
                return;
            }

            Integer count = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName, Integer.class);

            System.out.println(title + ": " + count + " records");


            if (count > 0 && count <= 10) {
                List<Map<String, Object>> sample = transactionsJdbcTemplate.queryForList(
                        "SELECT * FROM " + tableName + " LIMIT 2");

                if (!sample.isEmpty()) {
                    System.out.println("   Sample:");
                    for (int i = 0; i < sample.size(); i++) {
                        Map<String, Object> row = sample.get(i);
                        String preview = row.toString();
                        if (preview.length() > 50) {
                            preview = preview.substring(0, 47) + "...";
                        }
                        System.out.println("     " + (i + 1) + ". " + preview);
                    }
                    if (count > 2) {
                        System.out.println("     ... and " + (count - 2) + " more");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(title + ":  Error - " + e.getMessage());
        }
    }

    private boolean tableExists(String tableName) {
        try {
            Integer count = transactionsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = ?",
                    Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void quickHealthCheck() {
        System.out.println("\n QUICK HEALTH CHECK");


        assertNotNull(transactionsJdbcTemplate);
        Integer result = transactionsJdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);
        System.out.println("✅ Transactions DB: HEALTHY");


        assertTrue(tableExists("USERS"), "USERS should exist");
        assertTrue(tableExists("PRODUCTS"), "PRODUCTS should exist");
        assertTrue(tableExists("TRANSACTIONS"), "TRANSACTIONS should exist");
        System.out.println("✅ Essential tables: EXIST");

        System.out.println(" SYSTEM IS HEALTHY!");
    }
}
