package com.warehouse.management.api.rest.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request body for adjusting stock quantity")
public record AdjustStockRequest(
        @Schema(description = "Variant UUID — omit or set null for item-level stock",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID variantId,

        @Schema(description = "Quantity to add (positive) or remove (negative)", example = "50")
        @NotNull(message = "delta is required")
        Integer delta
) {}
