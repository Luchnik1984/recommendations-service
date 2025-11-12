package org.skypro.bank.star.recommendations_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.repository.RuleQueryValid;

@RuleQueryValid
public record RuleQueryDTO(
        @NotNull(message = "query не должен быть null") QueryType query,

        @NotNull(message = "arguments не должен быть null")
        @Size(min = 1, message = "arguments должен содержать хотя бы один элемент")
        String[] arguments,

        boolean negate) {}
