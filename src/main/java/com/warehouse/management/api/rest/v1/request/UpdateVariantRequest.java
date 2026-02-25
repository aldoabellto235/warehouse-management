package com.warehouse.management.api.rest.v1.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Request body for updating a variant")
public record UpdateVariantRequest(
        @Schema(description = "Variant display name", example = "Blue / Medium")
        @NotBlank(message = "Variant name is required")
        String name,

        @Schema(description = "Globally unique SKU (auto-uppercased)", example = "TSHIRT-BLUE-M")
        @NotBlank(message = "SKU is required")
        String sku,

        @Schema(description = "Variant price", example = "34.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be zero or positive")
        BigDecimal price,

        @Schema(description = "Key-value attribute map", example = "{\"color\":\"blue\",\"size\":\"M\"}")
        Map<String, String> attributes
) {}
