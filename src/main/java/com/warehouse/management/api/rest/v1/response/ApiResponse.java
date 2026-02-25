package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard success response wrapper for single data")
public record ApiResponse<T>(
        @Schema(description = "Response payload") T data,
        @Schema(description = "Response status", example = "success") String status
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "success");
    }
}
