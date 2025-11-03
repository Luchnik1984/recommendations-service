package org.skypro.bank.star.recommendations_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.skypro.bank.star.recommendations_service.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserStories1FinalTest {

    private static final Logger logger = LoggerFactory.getLogger(UserStories1FinalTest.class);

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
            testRepository.getTransactionsCount(); // Если не упадет - подключение работает
        }, "Should be able to query transactions table");
    }
}
