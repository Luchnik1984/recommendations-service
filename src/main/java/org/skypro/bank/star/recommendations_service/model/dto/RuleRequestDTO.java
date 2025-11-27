package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "Запрос на создание динамического правила")
public record RuleRequestDTO(
        @Schema(description = "Название продукта",
                example = "Премиальная кредитная карта",
                minLength = 2, maxLength = 1000)
        @JsonProperty("product_name")
        @NotNull(message = "productName не должен быть null")
        @Size(min = 2, max = 1000, message = "productName должно быть от 2 до 1000 символов")
        String productName,

        @Schema(description = "UUID продукта",
                example = "123e4567-e89b-12d3-a456-426614174000")
        @JsonProperty("product_id")
        @NotNull(message = "productId не должен быть null")
        UUID productId,

        @Schema(description = "Текст описания продукта",
                example = "Получите премиальную кредитную карту с повышенным кэшбэком 5% на все покупки",
                minLength = 2, maxLength = 1000)
        @JsonProperty("product_text")
        @NotNull(message = "productText не должен быть null")
        @Size(min = 2, max = 1000, message = "productText должно быть от 2 до 1000 символов")
        String productText,

        @Schema(description = "Список условий правила")
        @NotNull(message = "rule не должен быть null")
        @Size(min = 1, message = "rule должен содержать хотя бы один элемент")
        List<@Valid RuleQueryDTO> rule) {
}
