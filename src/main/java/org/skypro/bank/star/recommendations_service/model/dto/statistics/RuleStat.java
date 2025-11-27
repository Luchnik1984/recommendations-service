package org.skypro.bank.star.recommendations_service.model.dto.statistics;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * DTO для ответа статистики правил.
 */

public  class RuleStat {

    @JsonProperty("rule_id")
    private String ruleId;

    @JsonProperty("count")
    private Long count;

    public RuleStat() {
    }

    public RuleStat(String ruleId, Long count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "RuleStat{" +
                "ruleId='" + ruleId + '\'' +
                ", count=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleStat ruleStat = (RuleStat) o;
        return Objects.equals(ruleId, ruleStat.ruleId) && Objects.equals(count, ruleStat.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, count);
    }
}