package org.skypro.bank.star.recommendations_service.model.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * DTO для ответа статистики правил.
 * Формат ответа: {"stats": [{"rule_id": "...", "count": "..."}]}
 */
public class RuleStatsResponse {

    @JsonProperty("stats")
    private List<RuleStat> stats;

    public RuleStatsResponse() {
    }

    public RuleStatsResponse(List<RuleStat> stats) {
        this.stats = stats;
    }

    public List<RuleStat> getStats() {
        return stats;
    }

    public void setStats(List<RuleStat> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "RuleStatsResponse{" +
                "stats=" + stats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleStatsResponse that = (RuleStatsResponse) o;
        return Objects.equals(stats, that.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stats);
    }
}
