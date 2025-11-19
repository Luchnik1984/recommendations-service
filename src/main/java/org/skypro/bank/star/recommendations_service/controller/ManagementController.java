package org.skypro.bank.star.recommendations_service.controller;

import org.skypro.bank.star.recommendations_service.service.CacheManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management/clear-caches")
public class ManagementController {

    CacheManagementService cacheManagementService;

    public ManagementController(CacheManagementService cacheManagementService) {
        this.cacheManagementService = cacheManagementService;
    }

    @PostMapping("")
    public void clearAllCaches(){
        cacheManagementService.clearAllCaches();
    }
}
