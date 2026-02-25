package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Sale transaction record")
public record SaleResponse(
        @Schema(description = "Sale record UUID") UUID saleId,
        @Schema(description = "Item UUID") UUID itemId,
        @Schema(description = "Variant UUID — null for item-level sales") UUID variantId,
        @Schema(description = "Quantity sold", example = "2") int quantity,
        @Schema(description = "Timestamp when the sale occurred") Instant soldAt
) {}
