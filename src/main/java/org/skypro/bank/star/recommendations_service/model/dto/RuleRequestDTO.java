package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record RuleRequestDTO(

        @JsonProperty("product_name")
        @NotNull(message = "productName не должен быть null")
        @Size(min = 2, max = 50, message = "productName должно быть от 2 до 50 символов")
        String productName,

        @JsonProperty("product_id")
        @NotNull(message = "productId не должен быть null")
        UUID productId,

        @JsonProperty("product_text")
        @NotNull(message = "productText не должен быть null")
        @Size(min = 2, max = 50, message = "productText должно быть от 2 до 50 символов")
        String productText,

        @NotNull(message = "rule не должен быть null")
        @Size(min = 1, message = "rule должен содержать хотя бы один элемент")
        List<@Valid RuleQueryDTO> rule) {
}
