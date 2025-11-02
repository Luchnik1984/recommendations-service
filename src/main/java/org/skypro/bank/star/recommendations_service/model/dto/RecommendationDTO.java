package org.skypro.bank.star.recommendations_service.model.dto;

import java.util.UUID;

/**
 * DTO для рекомендации продукта.
 * Соответствует спецификации из ТЗ для массива recommendations
 */
public record RecommendationDTO(
        UUID id,        // ID продукта из ТЗ (например: "147f6a0f-3b91-413b-ab99-87f081d60d5a")
        String name,    // Название продукта (например: "Invest 500")
        String text     // Текстовое описание из ТЗ
) {
    public RecommendationDTO {
        // Валидация входных параметров
        if (id == null) throw new IllegalArgumentException("Product ID cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name cannot be empty");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("Product text cannot be empty");
    }
}
