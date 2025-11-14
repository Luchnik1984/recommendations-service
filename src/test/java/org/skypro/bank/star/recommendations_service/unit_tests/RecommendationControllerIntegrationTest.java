package org.skypro.bank.star.recommendations_service.unit_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RecommendationControllerIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationControllerIntegrationTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }
    private final UUID EXISTING_USER = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final UUID NON_EXISTENT_USER = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Test
    void testGetRecommendations_ValidUser() {
        logger.info("Testing GET /recommendation/{}", EXISTING_USER);

        ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                "/recommendation/" + EXISTING_USER,
                RecommendationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EXISTING_USER, response.getBody().userId());
        assertNotNull(response.getBody().recommendations());

        logger.info("Response: {}", response.getBody());
    }

    @Test
    void testGetRecommendations_NonExistentUser() {
        logger.info("Testing GET /recommendation/{}", NON_EXISTENT_USER);

        ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                "/recommendation/" + NON_EXISTENT_USER,
                RecommendationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(NON_EXISTENT_USER, response.getBody().userId());
        assertTrue(response.getBody().recommendations().isEmpty());

        logger.info("Response for non-existent user: {}", response.getBody());
    }

    @Test
    void testGetRecommendations_InvalidUserId() {
        logger.info("Testing GET /recommendation/ with invalid UUID");

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recommendation/invalid-uuid",
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        logger.info("Invalid UUID handled correctly");
    }
}
