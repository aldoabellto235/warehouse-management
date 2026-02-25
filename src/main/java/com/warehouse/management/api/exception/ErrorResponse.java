package com.warehouse.management.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard error response body")
public record ErrorResponse(
        @Schema(description = "Empty data object on error") Object data,
        @Schema(description = "Error details") ErrorDetail error,
        @Schema(description = "Response status", example = "error") String status
) {
    @Schema(description = "Error detail information")
    public record ErrorDetail(
            @Schema(description = "Timestamp of the error") Instant timestamp,
            @Schema(description = "HTTP status code", example = "404") int status,
            @Schema(description = "HTTP status name", example = "NOT_FOUND") String error,
            @Schema(description = "Human-readable error message") String message,
            @Schema(description = "Request path that triggered the error") String path
    ) {}

    public static ErrorResponse of(ErrorDetail detail) {
        return new ErrorResponse(Map.of(), detail, "error");
    }
}
