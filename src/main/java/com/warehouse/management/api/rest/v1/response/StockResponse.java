package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Stock level details")
public record StockResponse(
        @Schema(description = "Stock record UUID")
        UUID stockId,

        @Schema(description = "Item UUID")
        UUID itemId,

        @Schema(description = "Variant UUID — null means item-level stock")
        UUID variantId,

        @Schema(description = "Current quantity in stock", example = "100")
        int quantity,

        @Schema(description = "True if at least 1 unit is available", example = "true")
        boolean available
) {}
