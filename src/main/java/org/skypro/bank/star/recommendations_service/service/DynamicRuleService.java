package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.mapper.RuleMapper;
import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository) {
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    public RuleResponse postDynamicRule(RuleRequestDTO ruleRequestDTO){
        return RuleMapper.toResponse( dynamicRuleRepository.addDynamicRule());
    }

    public ListRuleResponse getListDynamicRule() {

        List<DynamicRule> list = dynamicRuleRepository.findAllByOrderById();

        List<RuleResponse> ruleResponses = list.stream()
                .map(RuleMapper::toResponse)
                .toList();
        return new ListRuleResponse(ruleResponses);
    }

    public void deleteDynamicRuleById(UUID id) {
            dynamicRuleRepository.deleteById(id);
    }
}
