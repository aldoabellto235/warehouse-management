package com.warehouse.management.api.rest.v1.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response returned after a successful resource creation")
public record CreatedResponse(
        @Schema(description = "UUID of the newly created resource",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id
) {}
