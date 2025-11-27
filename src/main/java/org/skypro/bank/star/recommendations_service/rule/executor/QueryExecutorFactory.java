package org.skypro.bank.star.recommendations_service.rule.executor;

import jakarta.annotation.PostConstruct;
import org.skypro.bank.star.recommendations_service.enums.QueryType;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Фабрика для получения исполнителей запросов динамических правил.
 * Реализует паттерн Фабрика для создания и управления исполнителями
 * различных типов запросов правил рекомендаций.
 */
@Component
public class QueryExecutorFactory {

    private final Map<QueryType, RuleQueryExecutor> ruleQueryExecutorMap = new HashMap<>();
    private final List<RuleQueryExecutor> ruleQueryExecutors;


    /**
     * Конструктор фабрики исполнителей запросов.
     * @param ruleQueryExecutors список всех доступных исполнителей,
     * автоматически внедряемых Spring
     */
    public QueryExecutorFactory(List<RuleQueryExecutor> ruleQueryExecutors, DynamicRuleRepository dynamicRuleRepository) {
        this.ruleQueryExecutors = ruleQueryExecutors;
    }

    /**
     * Метод создает HashMap с исполнителями правил, где ключ является QueryType,
     * а значение - сам класс исполнитель
     */
    @PostConstruct
    public void createMap() {
        for (RuleQueryExecutor ruleQueryExecutor : ruleQueryExecutors) {
            ruleQueryExecutorMap.put(ruleQueryExecutor.getSupportedQueryType(), ruleQueryExecutor);
        }
    }

    /**
     * Определяет обработчика для правила
     * @param ruleQuery правило
     * @return исполнитель
     */
    public RuleQueryExecutor getRuleQueryExecutor(RuleQuery ruleQuery){
        return ruleQueryExecutorMap.get(ruleQuery.getQuery());
    }

}
