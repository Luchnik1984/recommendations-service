package org.skypro.bank.star.recommendations_service.controller;

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
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    public DynamicRuleController(DynamicRuleService dynamicRuleService) {
        this.dynamicRuleService = dynamicRuleService;
    }


    @PostMapping("")
    public ResponseEntity<RuleResponse> postDynamicRule(
            @RequestBody @Valid RuleRequestDTO ruleRequestDTO) {
        return ResponseEntity.ok(dynamicRuleService.postDynamicRule(ruleRequestDTO));
    }

    @GetMapping("")
    public ResponseEntity<ListRuleResponse> getListDynamicRule(){
        return ResponseEntity.ok(dynamicRuleService.getListDynamicRule());
    }

    @DeleteMapping("{id}")
    public void deleteDynamicRuleById(@PathVariable("id")UUID id){
        dynamicRuleService.deleteDynamicRuleById(id);
    }
}
