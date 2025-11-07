package org.skypro.bank.star.recommendations_service.model.dto;

import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;

import java.util.List;
import java.util.UUID;

public record RuleRequestDTO (
        String product_name,
        UUID product_id,
        String product_text,
        List<RuleQuery> rule
        ){}
