package com.example.admin.entities.response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateTrainerEntityResponse(
        @Nonnull
        Long id,

        @Nonnull
        String title,

        @Nullable
        String description,

        @Nullable
        String difficultyLevel,

        @Nonnull
        Boolean isActive,

        @Nonnull
        LocalDateTime createdAt,

        @Nonnull
        LocalDateTime updatedAt
) {}