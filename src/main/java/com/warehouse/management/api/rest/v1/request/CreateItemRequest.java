package com.warehouse.management.api.rest.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request body for creating a new item")
public record CreateItemRequest(
        @Schema(description = "Item name", example = "T-Shirt")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Optional description", example = "100% cotton crew neck")
        String description,

        @Schema(description = "Base price (used when item has no variants)", example = "29.99")
        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.00", message = "Base price must be zero or positive")
        BigDecimal basePrice
) {}
