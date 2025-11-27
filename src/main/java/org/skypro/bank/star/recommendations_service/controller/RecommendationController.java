package org.skypro.bank.star.recommendations_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Рекомендации", description = "API для получения рекомендаций банковских продуктов")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(
            summary = "Получить рекомендации для пользователя",
            description = "Возвращает список рекомендованных банковских продуктов для указанного пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Рекомендации успешно получены",
                    content = @Content(schema = @Schema(implementation = RecommendationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "user_id": "123e4567-e89b-12d3-a456-426614174000",
                                              "recommendations": [
                                                {
                                                  "id": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                                                  "name": "Invest 500",
                                                  "text": "Откройте свой путь к успеху с индивидуальным инвестиционным счетом..."
                                                },
                                                {
                                                  "id": "59efc529-2fff-41af-baff-90ccd7402925",
                                                  "name": "Top Saving",
                                                  "text": "Откройте свою собственную Копилку с нашим банком!"
                                                }
                                              ]
                                            }
                                            """
                            ))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный ID пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })

    @GetMapping("/recommendation/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Parameter(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(response);
    }
}