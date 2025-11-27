package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ИНТЕГРАЦИОННЫЙ ТЕСТ - требует запущенный PostgreSQL.
 * Проверяет подключение ко всем базам данных в реальных условиях
 */

@SpringBootTest
@ActiveProfiles("integration-test")
public class DatabaseHealthCheckerTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthCheckerTest.class);

    @BeforeAll
    static void setup() {
        loadEnvFile("configuration.env");
        loadEnvFile("configuration.env.dev");
    }

    private static void loadEnvFile(String envFileName) {
        try {
            Path envPath = Paths.get(envFileName);
            if (!Files.exists(envPath)) {
                logger.warn("File {} not found", envFileName);
                return;
            }

            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(envPath));

            properties.forEach((key, value) -> {
                String keyStr = (String) key;
                String valueStr = (String) value;

                if (System.getProperty(keyStr) == null) {
                    System.setProperty(keyStr, valueStr);
                    String logValue = keyStr.toLowerCase().contains("password") ? "***" : valueStr;
                    logger.debug("Setting a variable: {}={}", keyStr, logValue);
                }
            });

            logger.info("Loaded {} variables from {}", properties.size(), envFileName);

        } catch (IOException e) {
            logger.error("Loading error {}: {}", envFileName, e.getMessage());
        }
    }

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate recommendationsJdbcTemplate;

    @Autowired
    private DynamicRuleRepository dynamicRuleRepository;

    /**
     *
     * ТЕСТ ПОДКЛЮЧЕНИЯ К Н2 БАЗЕ ДАННЫХ
     */

    @Test
    void testH2DatabaseConnection() {
        logger.info("=== TEST CONNECTION TO THE H2 DATABASE ===");

        try {
            Integer usersCount = recommendationsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM USERS", Integer.class);

            Integer productsCount = recommendationsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM PRODUCTS", Integer.class);

            Integer transactionsCount = recommendationsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);

            logger.info(" H2 the database is connected successfully");
            logger.info("   USERS: {} ", usersCount);
            logger.info("   PRODUCTS: {} ", productsCount);
            logger.info("   TRANSACTIONS: {} ", transactionsCount);

            assertNotNull(usersCount, "Must return number of users");
            assertNotNull(productsCount, "Must return number of products");
            assertNotNull(transactionsCount, "Must return the number of transactions");

            assertTrue(usersCount >= 0, "The number of users cannot be negative");
            assertTrue(productsCount >= 0, "The number of products cannot be negative");
            assertTrue(transactionsCount >= 0, "The number of products cannot be negative");

        } catch (Exception e) {
            logger.error(" Connection error to the H2 database: {}", e.getMessage());
            fail("Couldn't connect to H2 database: " + e.getMessage());
        }
    }

    /**
     *
     * ТЕСТ ПОДКЛЮЧЕНИЯ К POSTGRESQL БАЗЕ ДАННЫХ
     */
    @Test
    void testPostgreSQLDatabaseConnection() {
        logger.info("=== POSTGRESQL DATABASE CONNECTION TEST ===");

        String pgUrl = System.getProperty("POSTGRES_URL");
        String pgUser = System.getProperty("POSTGRES_USERNAME");

        logger.info("PostgreSQL URL: {}", pgUrl != null ? "entered" : "NOT entered");
        logger.info("PostgreSQL User: {}", pgUser != null ? "entered" : "NOT entered");

        assertNotNull(pgUrl, "POSTGRES_URL must be entered");
        assertTrue(pgUrl.startsWith("jdbc:postgresql:"), "POSTGRES_URL must be PostgreSQL URL");

        try {
            long rulesCount = dynamicRuleRepository.count();
            var allRules = dynamicRuleRepository.findAll();

            logger.info(" PostgreSQL database connected successfully");
            logger.info("   Dynamic rules: {}", rulesCount);
            logger.info("   Total records in the repository: {}", allRules.size());

            assertTrue(rulesCount >= 0, "number of rules cannot be negative");
            assertNotNull(allRules, "list of rules must not be null");

            assertDoesNotThrow(() -> {
                dynamicRuleRepository.count();
            }, "Must be able to make requests to PostgreSQL");

        } catch (Exception e) {
            logger.error(" Connection error to PostgreSQL database: {}", e.getMessage());
            fail("Couldn't connect to PostgreSQL database: " + e.getMessage());
        }
    }

    /**
     *
     * ТЕСТ РАБОТЫ ОБЕИХ БАЗ ДАННЫХ ОДНОВРЕМЕННО
     */
    @Test
    void testBothDatabasesWorkSimultaneously() {
        logger.info("=== TEST THE OPERATION OF BOTH DATABASES SIMULTANEOUSLY ===");

        try {
            Integer h2Count = recommendationsJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM USERS", Integer.class);

            long pgCount = dynamicRuleRepository.count();

            logger.info(" Both databases are running at the same time");
            logger.info("  H2 (users): {}, PostgreSQL (rules): {}", h2Count, pgCount);

            assertNotNull(h2Count, "H2 query should return a result");
            assertTrue(pgCount >= 0, "PostgreSQL зquery should return a result");

        } catch (Exception e) {
            logger.error(" Error when working with both databases: {}", e.getMessage());
            fail("Both databases should be running at the same time.: " + e.getMessage());
        }
    }
}
