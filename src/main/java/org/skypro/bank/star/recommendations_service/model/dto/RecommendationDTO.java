package org.skypro.bank.star.recommendations_service.model.dto;

import java.util.UUID;

/**
 * DTO для рекомендации продукта.
 * Соответствует спецификации из ТЗ для массива recommendations
 */
public record RecommendationDTO(
        UUID id,
        String name,
        String text
) {
    public RecommendationDTO {

        if (id == null){
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (text == null || text.isBlank()){
            throw new IllegalArgumentException("Product text cannot be empty");
        }
    }
}
