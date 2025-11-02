package org.skypro.bank.star.recommendations_service.controller;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendation/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable UUID userId) {
        RecommendationResponse response = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(response);
    }
}