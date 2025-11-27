package org.skypro.bank.star.recommendations_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.skypro.bank.star.recommendations_service.service.CacheManagementService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/management")
@Tag(name = "Управление сервисом", description = "API для управления и мониторинга сервиса")
public class ManagementController {

    private final CacheManagementService cacheManagementService;
    private final BuildProperties buildProperties;

    public ManagementController(CacheManagementService cacheManagementService,
                                BuildProperties buildProperties) {
        this.cacheManagementService = cacheManagementService;
        this.buildProperties = buildProperties;
    }

    /**
     * Эндпоинт для принудительной очистки всех кэшей сервиса.
     * POST /management/clear-caches
     * Соответствует требованию - сброс кэша рекомендаций
     */
    @Operation(summary = "Очистить все кеши сервиса")
    @ApiResponse(responseCode = "200", description = "Кеши успешно очищены")
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearAllCaches() {
        cacheManagementService.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    /**
     * Эндпоинт для получения информации о сборке сервиса.
     * GET /management/info
     * Соответствует требованию части 3: информация из pom.xml
     */
    @Operation(summary = "Получить информацию о сервисе")
    @ApiResponse(responseCode = "200", description = "Информация о сервисе получена",
            content = @Content(examples = @ExampleObject(
                    value = """
                            {
                              "name": "recommendations-service",
                              "version": "0.0.1-SNAPSHOT"
                            }
                            """
            ))
    )
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        ));
    }
}
