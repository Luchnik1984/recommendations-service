package org.skypro.bank.star.recommendations_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "rule_statistics")
public class RuleStatistics {

    @Id
    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "trigger_count", nullable = false)
    private Long triggerCount = 0L;

    public RuleStatistics() {
    }

    public RuleStatistics(UUID ruleId) {
        this.ruleId = ruleId;
        this.triggerCount = 0L;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public Long getTriggerCount() {
        return triggerCount;
    }

    public void setTriggerCount(Long triggerCount) {
        this.triggerCount = triggerCount;
    }
}