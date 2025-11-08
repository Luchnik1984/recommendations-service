package org.skypro.bank.star.recommendations_service.mapper;

import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;

public class RuleMapper {

    /**
     * Преобразует DynamicRule (сущность) в RuleResponse (DTO)
     */
    public static RuleResponse toResponse(DynamicRule dynamicRule) {
        if (dynamicRule == null) {
            return null;
        }

        return new RuleResponse(
                dynamicRule.getId(),
                dynamicRule.getProductName(),
                dynamicRule.getProductId(),
                dynamicRule.getProductText(),
                dynamicRule.getRule().stream()
                        .map(RuleMapper::ruleQueryToDTO)
                        .toList());
    }

    public static RuleQueryDTO ruleQueryToDTO(RuleQuery entity) {
        if (entity == null) {
            return null;
        }

        String[] arguments = entity.getArguments().toArray(new String[0]);

        return new RuleQueryDTO(
                entity.getQuery(),
                arguments,
                entity.isNegate());
    }
}

