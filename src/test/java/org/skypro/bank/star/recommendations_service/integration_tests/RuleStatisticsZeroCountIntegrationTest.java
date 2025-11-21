package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class RuleStatisticsZeroCountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = "/db.changelog/test-data/changes/init-test-statistics.sql")
    void countZeroRulesAreReturnedInApi() throws Exception {
        String ruleIdWithZero = "d3333333-3333-3333-3333-333333333333"; // есть в init-test-statistics.sql с count=0

        mockMvc.perform(get("/rule/stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // проверяем, что объект с этим rule_id и count=0 присутствует в массиве stats
                .andExpect(jsonPath(
                        "$.stats[?(@.rule_id=='" + ruleIdWithZero + "' && @.count==0)]"
                ).exists());
    }
}