package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.mapper.RuleMapper;
import org.skypro.bank.star.recommendations_service.model.statistics.RuleStatistics;
import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.model.dynamic.DynamicRule;
import org.skypro.bank.star.recommendations_service.repository.statistics.RuleStatisticsRepository;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления динамическими правилами рекомендаций.
 * Обеспечивает бизнес-логику работы с правилами.
 * Интегрирована система статистики срабатываний правил.
 */
@Service
public class DynamicRuleService {

    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatisticsRepository ruleStatisticsRepository;
    private static final Logger log = LoggerFactory.getLogger(DynamicRuleService.class);

    public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                              RuleStatisticsRepository ruleStatisticsRepository) {
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.ruleStatisticsRepository = ruleStatisticsRepository;
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

        // Создание статистики при создании правила
        RuleStatistics statistics = new RuleStatistics(savedDynamicRule.getId());
        ruleStatisticsRepository.save(statistics);
        log.info("Created dynamic rule {} with statistics initialized", savedDynamicRule.getId());

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
     * US16: При удалении правила автоматически удаляется RuleStatistics (каскадное удаление).
     * @param id идентификатор правила для удаления
     */
    @Transactional
    public void deleteDynamicRuleById(UUID id) {
        DynamicRule rule = dynamicRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule with id " + id + " not found"));

        // Удаление статистики при удалении правила
        ruleStatisticsRepository.deleteByRuleId(id);
        log.info("Deleted statistics for rule {}", id);

        dynamicRuleRepository.delete(rule);
        log.info("Deleted dynamic rule {}", id);
    }

    /**
     * Проверяет наличие статистики для правила.
     * @param ruleId идентификатор правила
     * @return true если статистика существует
     */
    public boolean hasStatistics(UUID ruleId) {
        return ruleStatisticsRepository.existsById(ruleId);
    }


    /**
     * Создание статистики для существующего правила (если её ещё нет).
     * Используется для гарантирования наличия записей для всех правил.
     *
     * @param ruleId идентификатор правила
     */
    @Transactional
    public void ensureStatisticsExists(UUID ruleId) {
        if (!ruleStatisticsRepository.existsById(ruleId)) {
            RuleStatistics statistics = new RuleStatistics(ruleId);
            ruleStatisticsRepository.save(statistics);
            log.info("Created missing statistics for rule {}", ruleId);
        }
    }
}

