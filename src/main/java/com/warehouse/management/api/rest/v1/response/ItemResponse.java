package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Item details")
public record ItemResponse(
        @Schema(description = "Item UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Item name", example = "T-Shirt")
        String name,

        @Schema(description = "Item description", example = "100% cotton crew neck")
        String description,

        @Schema(description = "Base price", example = "29.99")
        BigDecimal basePrice,

        @Schema(description = "Creation timestamp")
        Instant createdAt,

        @Schema(description = "Last update timestamp")
        Instant updatedAt
) {}
