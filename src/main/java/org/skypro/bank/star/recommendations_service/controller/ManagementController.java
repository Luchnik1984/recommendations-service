package org.skypro.bank.star.recommendations_service.controller;

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
public class ManagementController {

    private final CacheManagementService cacheManagementService;
    private final BuildProperties buildProperties;

    public ManagementController(CacheManagementService cacheManagementService,
                                BuildProperties buildProperties)
    {
        this.cacheManagementService = cacheManagementService;
        this.buildProperties = buildProperties;
    }

    /**
     * Эндпоинт для принудительной очистки всех кэшей сервиса.
     * POST /management/clear-caches
     * Соответствует требованию - сброс кэша рекомендаций
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<Void> clearAllCaches(){
        cacheManagementService.clearAllCaches();
        return ResponseEntity.ok().build();
    }

    /**
     * Эндпоинт для получения информации о сборке сервиса.
     * GET /management/info
     * Соответствует требованию части 3: информация из pom.xml
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
                "name", buildProperties.getName(),
                "version", buildProperties.getVersion()
        ));
    }
}
