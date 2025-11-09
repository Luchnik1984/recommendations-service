package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skypro.bank.star.recommendations_service.model.dynamic.RuleQuery;

import java.util.List;
import java.util.UUID;

public record RuleRequestDTO (

        @JsonProperty("product_name")
        String productName,

        @JsonProperty("product_id")
        UUID productId,

        @JsonProperty("product_text")
        String productText,

        List<RuleQueryDTO> rule){}
