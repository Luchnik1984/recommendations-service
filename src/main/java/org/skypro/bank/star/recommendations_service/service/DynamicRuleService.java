package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.mapper.RuleMapper;
import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Выполняет преобразование DTO в сущность и сохраняет в базу данных.
     * @param ruleRequestDTO DTO с данными для создания правила
     * @return DTO созданного правила
     */
    @Transactional
    public RuleResponse postDynamicRule(RuleRequestDTO ruleRequestDTO) {
        if (dynamicRuleRepository.existsByProductId(ruleRequestDTO.productId())) {
            throw new IllegalArgumentException(
                    "Rule with productId " + ruleRequestDTO.productId() + " already exists"
            );
        }

        DynamicRule dynamicRule = RuleMapper.dtoToDynamicRule(ruleRequestDTO);
        DynamicRule savedDynamicRule = dynamicRuleRepository.save(dynamicRule);

        return RuleMapper.toResponse(savedDynamicRule);
    }

    /**
     * Получает список всех динамических правил (GET /rule).
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
    @Transactional
    public void deleteDynamicRuleById(UUID id) {
        DynamicRule rule = dynamicRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule with id " + id + " not found"));
        dynamicRuleRepository.delete(rule);
    }
}
