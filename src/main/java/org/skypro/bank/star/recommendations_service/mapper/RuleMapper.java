package org.skypro.bank.star.recommendations_service.mapper;

import org.skypro.bank.star.recommendations_service.model.dto.RuleQueryDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuleMapper {

    /**
     * Преобразует DynamicRule (сущность) в RuleResponse (DTO)
     * @param dynamicRule сущность правила из базы данных
     * @return DTO для ответа API или null, если сущность равна null
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

    /**
     * Преобразует RuleQuery (сущность) в RuleQueryDTO.
     * @param entity сущность условия правила
     * @return DTO условия правила или null, если сущность равна null
     */
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

    /**
     * Преобразует RuleRequestDTO в DynamicRule (сущность).
     * @param dynamicRuleDTO DTO с данными для создания правила
     * @return сущность DynamicRule или null, если DTO равен null
     */
    public static DynamicRule dtoToDynamicRule(RuleRequestDTO dynamicRuleDTO) {
        if (dynamicRuleDTO == null) {
            return null;
        }

        DynamicRule dynamicRule = new DynamicRule();

        dynamicRule.setProductName(dynamicRuleDTO.productName());
        dynamicRule.setProductId(dynamicRuleDTO.productId());
        dynamicRule.setProductText(dynamicRuleDTO.productText());

        List<RuleQuery> ruleQueries =new ArrayList<>();
        if (dynamicRuleDTO.rule() != null) {
            for (int i = 0; i < dynamicRuleDTO.rule().size(); i++) {
                RuleQueryDTO queryDTO = dynamicRuleDTO.rule().get(i);
                RuleQuery ruleQuery = new RuleQuery(
                        queryDTO.query(),
                        Arrays.asList(queryDTO.arguments()),
                        queryDTO.negate(),
                        i
                );
                ruleQuery.setDynamicRule(dynamicRule);
                ruleQueries.add(ruleQuery);
            }
        }
        dynamicRule.setRule(ruleQueries);
        return dynamicRule;
    }

    /**
     * Преобразует RuleQueryDTO в RuleQuery (сущность).
     * @param ruleQueryDTO DTO условия правила
     * @return сущность RuleQuery или null, если DTO равен null
     */
    public static RuleQuery dtoToRuleQuery(RuleQueryDTO ruleQueryDTO) {

       RuleQuery ruleQuery = new RuleQuery();

       ruleQuery.setQuery(ruleQueryDTO.query());
       ruleQuery.setArguments(Arrays.asList(ruleQueryDTO.arguments()));
       ruleQuery.setNegate(ruleQueryDTO.negate());

       return ruleQuery;
    }

    /**
     * Преобразует список сущностей DynamicRule в список DTO RuleResponse.
     * @param dynamicRules список сущностей правил
     * @return список DTO для ответа API
     */
    public static List<RuleResponse> toResponseList(List<DynamicRule> dynamicRules) {
        if (dynamicRules == null) {
            return new ArrayList<>();
        }

        List<RuleResponse> responses = new ArrayList<>();
        for (DynamicRule dynamicRule : dynamicRules) {
            RuleResponse response = toResponse(dynamicRule);
            if (response != null) {
                responses.add(response);
            }
        }
        return responses;
    }
}

