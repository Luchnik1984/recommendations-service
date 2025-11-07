package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DynamicRuleService {

    public RuleResponse postDynamicRule(RuleRequestDTO ruleRequestDTO){
        return null;
    }

    public ListRuleResponse getListDynamicRule() {
        return null;
    }

    public void deleteDynamicRuleById(UUID id) {
    }
}
