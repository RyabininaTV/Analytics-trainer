package com.example.trainers.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record TrainerInfoByIdResponse(

    @Nonnull
    @JsonProperty(value = "id", required = true)
    Long id,

    @Nonnull
    @JsonProperty(value = "title", required = true)
    String title,

    @Nullable
    @JsonProperty("description")
    String description,

    @Nullable
    @JsonProperty("difficulty_level")
    String difficultyLevel,

    @Nonnull
    @JsonProperty(value = "is_active", required = true)
    Boolean isActive,

    @Nonnull
    @JsonProperty(value = "created_at", required = true)
    LocalDateTime createdAt,

    @Nonnull
    @JsonProperty(value = "updated_at", required = true)
    LocalDateTime updatedAt

) {}
