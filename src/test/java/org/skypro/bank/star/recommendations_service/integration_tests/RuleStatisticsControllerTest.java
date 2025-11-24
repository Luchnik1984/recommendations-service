package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RuleStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllStats_returnsOkAndList() throws Exception {
        mockMvc.perform(get("/rule/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stats").isArray());
    }

    @Test
    void getStatsSummary_returnsOkAndSummaryFields() throws Exception {
        mockMvc.perform(get("/rule/stats/summary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_triggers").exists())
                .andExpect(jsonPath("$.unused_rules_count").exists());
    }

    @Test
    @Sql(scripts = "classpath:db/changelog/test-data/changes/init-test-statistics.sql")
    void getSpecificRuleStat_returns200Or404() throws Exception {

        String existingRuleId = "d1111111-1111-1111-1111-111111111111";

        mockMvc.perform(get("/rule/stats/" + existingRuleId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rule_id").value(existingRuleId));

        /* Тест для несуществующего ID */
        String notExistingRuleId = "99999999-9999-9999-9999-999999999999";
        mockMvc.perform(get("/rule/stats/" + notExistingRuleId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}