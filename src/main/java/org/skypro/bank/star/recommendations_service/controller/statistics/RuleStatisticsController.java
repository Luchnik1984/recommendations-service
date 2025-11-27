package org.skypro.bank.star.recommendations_service.controller.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Статистика правил", description = "API для получения статистики срабатываний правил")
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
    @Operation(summary = "Получить статистику срабатываний всех правил")
    @ApiResponse(responseCode = "200", description = "Статистика успешно получена")
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
    @Operation(summary = "Получить статистику срабатываний правила по Id")
    @ApiResponse(responseCode = "200", description = "Статистика успешно получена",
            content = @Content(schema = @Schema(implementation = RuleStatsResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "stats": [
                                        {
                                          "rule_id": "d1111111-1111-1111-1111-111111111111",
                                          "count": 15
                                        },
                                        {
                                          "rule_id": "d2222222-2222-2222-2222-222222222222",
                                          "count": 8
                                        },
                                        {
                                          "rule_id": "d3333333-3333-3333-3333-333333333333",
                                          "count": 0
                                        }
                                      ]
                                    }
                                    """
                    ))
    )
    @ApiResponse(responseCode = "404", description = "Правило не найдено")
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

