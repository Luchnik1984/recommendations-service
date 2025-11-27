package org.skypro.bank.star.recommendations_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.repository.RuleQueryValid;

@RuleQueryValid
@Schema(description = "Условие динамического правила")
public record RuleQueryDTO(
        @Schema(description = "Тип запроса", example = "USER_OF", implementation = QueryType.class)
        @NotNull(message = "query не должен быть null") QueryType query,

        @Schema(description = "Аргументы запроса", example = "[\"DEBIT\"]")
        @NotNull(message = "arguments не должен быть null")
        @Size(min = 1, message = "arguments должен содержать хотя бы один элемент")
        String[] arguments,

        @Schema(description = "Флаг отрицания результата", example = "false")
        boolean negate) {
}
