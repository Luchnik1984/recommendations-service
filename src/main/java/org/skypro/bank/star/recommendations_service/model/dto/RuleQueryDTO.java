package org.skypro.bank.star.recommendations_service.model.dto;

import org.skypro.bank.star.recommendations_service.enums.QueryType;

public record RuleQueryDTO(
        QueryType query,
        String[] arguments,
        boolean negate) {}
