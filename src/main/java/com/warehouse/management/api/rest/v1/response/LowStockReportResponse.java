package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A single low-stock alert entry")
public record LowStockReportResponse(
        @Schema(description = "Item UUID") UUID itemId,
        @Schema(description = "Item name", example = "T-Shirt") String itemName,
        @Schema(description = "Variant UUID — null for item-level stock") UUID variantId,
        @Schema(description = "Variant name — null for item-level stock", example = "Red / Large") String variantName,
        @Schema(description = "SKU — null for item-level stock", example = "TSHIRT-RED-L") String sku,
        @Schema(description = "Current quantity in stock", example = "2") int quantity,
        @Schema(description = "Alert threshold used in this query", example = "5") int threshold
) {}
