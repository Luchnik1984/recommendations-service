package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.mapper.RuleMapper;
import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.repository.DynamicRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления динамическими правилами рекомендаций.
 * Обеспечивает бизнес-логику работы с правилами.
 */
@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository) {
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Создает новое динамическое правило.
     * @param ruleRequestDTO DTO с данными для создания правила
     * @return DTO созданного правила
     */
    public RuleResponse postDynamicRule(RuleRequestDTO ruleRequestDTO){
        DynamicRule entity = RuleMapper.dtoToDynamicRule(ruleRequestDTO);
        DynamicRule saved = dynamicRuleRepository.save(entity);

        return RuleMapper.toResponse(saved);
    }

    /**
     * Получает список всех динамических правил.
     * @return DTO со списком всех правил
     */
    public ListRuleResponse getListDynamicRule() {
        List<DynamicRule> list = dynamicRuleRepository.findAllByOrderById();

        List<RuleResponse> ruleResponses = list.stream()
                .map(RuleMapper::toResponse)
                .toList();
        return new ListRuleResponse(ruleResponses);
    }

    /**
     * Удаляет динамическое правило по идентификатору.
     * @param id идентификатор правила для удаления
     */
    public void deleteDynamicRuleById(UUID id) {
        dynamicRuleRepository.deleteById(id);
    }

}
