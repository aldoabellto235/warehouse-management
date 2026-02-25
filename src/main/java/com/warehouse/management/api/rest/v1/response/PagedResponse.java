package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standard success response wrapper for paginated data")
public record PagedResponse<T>(
        @Schema(description = "List of results") List<T> data,
        @Schema(description = "Pagination metadata") PageAttribute attribute,
        @Schema(description = "Response status", example = "success") String status
) {
    public static <T> PagedResponse<T> success(List<T> data, int page, int size, long totalSize) {
        return new PagedResponse<>(data, new PageAttribute(page, size, totalSize), "success");
    }
}
