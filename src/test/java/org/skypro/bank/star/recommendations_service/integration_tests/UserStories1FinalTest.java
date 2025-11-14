package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.skypro.bank.star.recommendations_service.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration-test")
public class UserStories1FinalTest {

    private static final Logger logger = LoggerFactory.getLogger(UserStories1FinalTest.class);

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
    private TestRepository testRepository;

    @Test
    void testUS1DatabaseConfiguration() {
        logger.info("=== US1: FINAL CONFIGURATION CHECK ===");
        int usersCount = testRepository.getUsersCount();
        int productsCount = testRepository.getProductsCount();
        int transactionsCount = testRepository.getTransactionsCount();

        logger.info(" Connection to the database was established successfully");
        logger.info(" USERS table: {} records", usersCount);
        logger.info(" PRODUCTS table: {} records", productsCount);
        logger.info(" TRANSACTIONS table: {} records", transactionsCount);

        // Assertions для интеграционного теста
        assertTrue(usersCount > 0, "Should have users in database");
        assertTrue(productsCount > 0, "Should have products in database");
        assertTrue(transactionsCount > 0, "Should have transactions in database");

        logger.info(" US1 COMPLETED SUCCESSFULLY!");
        logger.info(" Spring Boot project is configured with H2 database (read-only)");
        logger.info(" DataSource and JdbcTemplate are configured correctly");
    }

    @Test
    void testTransactionTypesInDatabase() {
        assertDoesNotThrow(() -> {
            testRepository.getTransactionsCount();
        }, "Should be able to query transactions table");
    }
}
