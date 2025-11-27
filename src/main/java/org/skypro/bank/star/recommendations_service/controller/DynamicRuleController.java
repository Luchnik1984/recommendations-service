package org.skypro.bank.star.recommendations_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.skypro.bank.star.recommendations_service.model.dto.ListRuleResponse;
import org.skypro.bank.star.recommendations_service.model.dto.RuleRequestDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RuleResponse;
import org.skypro.bank.star.recommendations_service.service.DynamicRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rule")
@Tag(name = "Динамические правила", description = "API для управления динамическими правилами рекомендаций")
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }

    @Operation(summary = "Создать новое динамическое правило")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило успешно создано",
                    content = @Content(schema = @Schema(implementation = RuleResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                              "product_name": "Премиальная кредитная карта",
                                              "product_id": "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
                                              "product_text": "Получите премиальную кредитную карту с повышенным кэшбэком",
                                              "rule": [
                                                {
                                                  "query": "USER_OF",
                                                  "arguments": ["DEBIT"],
                                                  "negate": false
                                                },
                                                {
                                                  "query": "TRANSACTION_SUM_COMPARE",
                                                  "arguments": ["DEBIT", "DEPOSIT", ">", "50000"],
                                                  "negate": false
                                                }
                                              ]
                                            }
                                            """
                            ))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные правила")
    })
    @PostMapping("")
    public ResponseEntity<RuleResponse> postDynamicRule(
            @io.swagger.v3.oas.annotations.parameters
                    .RequestBody(description = "Данные для создания правила",
                    content = @Content(examples = @ExampleObject(
                            value = """
                                    {
                                      "product_name": "Премиальная кредитная карта",
                                      "product_id": "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
                                      "product_text": "Получите премиальную кредитную карту с повышенным кэшбэком 5% на все покупки",
                                      "rule": [
                                        {
                                          "query": "USER_OF",
                                          "arguments": ["DEBIT"],
                                          "negate": false
                                        },
                                        {
                                          "query": "TRANSACTION_SUM_COMPARE",
                                          "arguments": ["DEBIT", "DEPOSIT", ">", "50000"],
                                          "negate": false
                                        }
                                      ]
                                    }
                                    """
                    ))
            )
            @RequestBody @Valid RuleRequestDTO ruleRequestDTO) {
        return ResponseEntity.ok(dynamicRuleService.postDynamicRule(ruleRequestDTO));
    }

    @Operation(summary = "Получить все динамические правила")
    @ApiResponse(responseCode = "200", description = "Список правил успешно получен",
            content = @Content(schema = @Schema(implementation = ListRuleResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                      "data": [
                                        {
                                          "id": "d1111111-1111-1111-1111-111111111111",
                                          "product_name": "Премиальная кредитная карта",
                                          "product_id": "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
                                          "product_text": "Получите премиальную кредитную карту с повышенным кэшбэком",
                                          "rule": [
                                            {
                                              "query": "USER_OF",
                                              "arguments": ["DEBIT"],
                                              "negate": false
                                            }
                                          ]
                                        },
                                        {
                                          "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                          "product_name": "Инвестиционный счет",
                                          "product_id": "ffffffff-ffff-ffff-ffff-ffffffffffff",
                                          "product_text": "Начните инвестировать с нашим брокерским счетом",
                                          "rule": [
                                            {
                                              "query": "ACTIVE_USER_OF",
                                              "arguments": ["DEBIT"],
                                              "negate": false
                                            }
                                          ]
                                        }
                                      ]
                                    }
                                    """
                    ))
    )
    @GetMapping("")
    public ResponseEntity<ListRuleResponse> getListDynamicRule() {
        return ResponseEntity.ok(dynamicRuleService.getListDynamicRule());
    }

    @Operation(summary = "Удалить динамическое правило")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Правило успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Правило не найдено")
    })
    @DeleteMapping("{id}")
    public void deleteDynamicRuleById(
            @Parameter(description = "UUID правила",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        dynamicRuleService.deleteDynamicRuleById(id);
    }
}
