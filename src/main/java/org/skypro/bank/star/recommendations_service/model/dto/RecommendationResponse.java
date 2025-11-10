package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * DTO для ответа API.
 * Соответствует спецификации из ТЗ для основного ответа
 *
 * @param userId          - ID пользователя из запроса
 * @param recommendations - Список подходящих продуктов
 */

public record RecommendationResponse(

        @JsonProperty("user_id")
        UUID userId,
        List<RecommendationDTO> recommendations
) {
    public RecommendationResponse {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (recommendations == null) {
            recommendations = List.of();
        }
    }

    public RecommendationResponse(UUID userId) {
        this(userId, List.of());
    }
}
