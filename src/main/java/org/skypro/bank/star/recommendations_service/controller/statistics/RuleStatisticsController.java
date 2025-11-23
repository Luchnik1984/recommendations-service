package org.skypro.bank.star.recommendations_service.controller.statistics;

import org.skypro.bank.star.recommendations_service.model.dto.statistics.RuleStat;
import org.skypro.bank.star.recommendations_service.model.dto.statistics.RuleStatsResponse;
import org.skypro.bank.star.recommendations_service.service.statistics.RuleStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleStatisticsController {

    private static final Logger log = LoggerFactory.getLogger(RuleStatisticsController.class);

    private final RuleStatisticsService statisticsService;

    public RuleStatisticsController(RuleStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Получение статистики срабатываний всех правил.
     * Возвращает статистику в JSON формате: {"stats": [{"rule_id": "...", "count": "..."}]}
     */
    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getRuleStatistics() {
        try {
            log.info("Received GET /rule/stats request");
            RuleStatsResponse response = statisticsService.getAllStatistics();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting rule statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получение статистики для конкретного правила.
     * Возвращает JSON {"rule_id": "...", "count": ...} или 404 если не найдено.
     */
    @GetMapping("/stats/{ruleId}")
    public ResponseEntity<RuleStat> getRuleStatisticById(@PathVariable String ruleId) {
        try {
            log.info("Received GET /rule/stats/{} request", ruleId);
            RuleStat stat = statisticsService.getStatisticsByRuleId(ruleId);

            if (stat == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(stat);
        } catch (Exception e) {
            log.error("Error getting rule statistics for {}", ruleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получение общей сводной статистики по всем правилам.
     */
    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getStatisticsSummary() {
        try {
            log.info("Received GET /rule/stats/summary request");
            Long totalTriggers = statisticsService.getTotalTriggers();
            Long unusedRules = statisticsService.getUnusedRulesCount();

            Map<String, Object> summary = new HashMap<>();
            summary.put("total_triggers", totalTriggers);
            summary.put("unused_rules_count", unusedRules);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting statistics summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

