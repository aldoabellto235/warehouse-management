package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Variant details")
public record VariantResponse(
        @Schema(description = "Variant UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Parent item UUID")
        UUID itemId,

        @Schema(description = "Variant display name", example = "Red / Large")
        String name,

        @Schema(description = "Stock-keeping unit", example = "TSHIRT-RED-L")
        String sku,

        @Schema(description = "Variant price", example = "29.99")
        BigDecimal price,

        @Schema(description = "Variant attributes", example = "{\"color\":\"red\",\"size\":\"L\"}")
        Map<String, String> attributes,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Last update timestamp")
        Instant updatedAt
) {}
