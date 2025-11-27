package org.skypro.bank.star.recommendations_service.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;


public class OpenApiConfig {

    @Bean
    public OpenAPI recommendationsServiceOpenAPI(BuildProperties buildProperties) {

        return new OpenAPI()
                .info(new Info()
                        .title("Bank Star - Recommendations Service API")
                        .description("""
                                REST API для рекомендательной системы банка "Стар".
                                Сервис анализирует финансовое поведение клиентов и рекомендует
                                подходящие банковские продукты согласно бизнес-правилам.
                                """)
                        .version(buildProperties.getVersion())
                );

    }
}
