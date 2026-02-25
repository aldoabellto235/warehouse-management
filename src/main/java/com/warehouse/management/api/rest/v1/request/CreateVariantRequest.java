package com.warehouse.management.api.rest.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Request body for adding a variant to an item")
public record CreateVariantRequest(
        @Schema(description = "Variant display name", example = "Red / Large")
        @NotBlank(message = "Variant name is required")
        String name,

        @Schema(description = "Globally unique stock-keeping unit (auto-uppercased)", example = "TSHIRT-RED-L")
        @NotBlank(message = "SKU is required")
        String sku,

        @Schema(description = "Variant price (overrides item base price)", example = "29.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be zero or positive")
        BigDecimal price,

        @Schema(description = "Key-value attribute map", example = "{\"color\":\"red\",\"size\":\"L\"}")
        Map<String, String> attributes
) {}
