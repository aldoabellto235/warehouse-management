package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Item with all variants and current stock levels")
public record ItemSummaryResponse(
        @Schema(description = "Item UUID") UUID id,
        @Schema(description = "Item name", example = "T-Shirt") String name,
        @Schema(description = "Item description") String description,
        @Schema(description = "Base price", example = "29.99") BigDecimal basePrice,
        @Schema(description = "True if item has variants") boolean hasVariants,
        @Schema(description = "Total units in stock across all variants", example = "150") int totalStock,
        @Schema(description = "Variant list with stock levels") List<VariantStockResponse> variants,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Last update timestamp") Instant updatedAt
) {
    @Schema(description = "Variant with its current stock level")
    public record VariantStockResponse(
            @Schema(description = "Variant UUID") UUID variantId,
            @Schema(description = "Variant name", example = "Red / Large") String name,
            @Schema(description = "SKU", example = "TSHIRT-RED-L") String sku,
            @Schema(description = "Variant price", example = "29.99") BigDecimal price,
            @Schema(description = "Attributes", example = "{\"color\":\"red\",\"size\":\"L\"}") Map<String, String> attributes,
            @Schema(description = "Current quantity in stock", example = "50") int stock,
            @Schema(description = "True if at least 1 unit is available") boolean available
    ) {}
}
