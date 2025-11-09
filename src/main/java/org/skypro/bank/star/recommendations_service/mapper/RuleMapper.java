package org.skypro.bank.star.recommendations_service.mapper;

import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;

import java.util.Arrays;

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

    public static DynamicRule dtoToDynamicRule(RuleRequestDTO dynamicRuleDTO) {

        DynamicRule dynamicRule = new DynamicRule();

        dynamicRule.setProductName(dynamicRuleDTO.productName());
        dynamicRule.setProductId(dynamicRuleDTO.productId());
        dynamicRule.setProductText(dynamicRuleDTO.productText());
        dynamicRule.setRule(dynamicRuleDTO.rule());
        return dynamicRule;
    }

    public static RuleQuery dtoToRuleQuery(RuleQueryDTO ruleQueryDTO) {

       RuleQuery ruleQuery = new RuleQuery();

       ruleQuery.setQuery(ruleQueryDTO.query());
       ruleQuery.setArguments(Arrays.asList(ruleQueryDTO.arguments()));
       ruleQuery.setNegate(ruleQueryDTO.negate());

       return ruleQuery;
    }
}

