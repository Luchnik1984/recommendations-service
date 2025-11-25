package org.skypro.bank.star.recommendations_service.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class OpenApiConfig {

    @Bean
    public OpenAPI recommendationsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Star - Recommendations Service API")
                        .description("""
                                REST API для рекомендательной системы банка "Стар".
                                Сервис анализирует финансовое поведение клиентов и рекомендует
                                подходящие банковские продукты согласно бизнес-правилам.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bank Star Development Team")
                                .email("dev@bankstar.ru")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Локальный сервер разработки"),
                        new Server()
                                .url("https://api.bankstar.ru")
                                .description("Продакшн сервер")
                ));
    }
}
