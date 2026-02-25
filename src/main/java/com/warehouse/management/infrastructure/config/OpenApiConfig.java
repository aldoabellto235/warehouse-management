package com.warehouse.management.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI warehouseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse Management API")
                        .description("""
                                RESTful API for managing a shop's warehouse inventory — items, variants, pricing, and stock.

                                ## Standard Response Format

                                **Success (single)**
                                ```json
                                { "data": { ... }, "status": "success" }
                                ```

                                **Success (paginated)**
                                ```json
                                { "data": [...], "attribute": { "page": 0, "size": 20, "totalSize": 300 }, "status": "success" }
                                ```

                                **Error**
                                ```json
                                { "data": {}, "error": { "timestamp": "...", "status": 404, "error": "NOT_FOUND", "message": "...", "path": "..." }, "status": "error" }
                                ```
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Warehouse Team")))
                .servers(List.of(
                        new Server().url("http://localhost:8585").description("Local development")
                ));
    }
}
