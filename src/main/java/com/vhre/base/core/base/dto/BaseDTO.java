package com.vhre.base.core.base.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Data Transfer Object containing common attributes for all payloads.
 */
@Data
public abstract class BaseDTO {
    @Schema(
            description = "Unique identifier of the resource (UUID). Automatically generated.",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Null(message = "The ID must be null when creating a new resource")
    private UUID id;

    @Schema(
            description = "Timestamp indicating when the resource was created.",
            example = "2023-10-25T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Timestamp indicating the last time the resource was updated.",
            example = "2023-10-25T14:30:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "Flag indicating if the resource has been soft-deleted.",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private boolean deleted;
}
