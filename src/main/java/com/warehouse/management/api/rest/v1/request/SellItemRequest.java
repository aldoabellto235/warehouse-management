package com.warehouse.management.api.rest.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request body for selling (deducting) stock")
public record SellItemRequest(
        @Schema(description = "Variant UUID — omit or set null for item-level stock",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID variantId,

        @Schema(description = "Quantity to sell (must be at least 1)", example = "2")
        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
