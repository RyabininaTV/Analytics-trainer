package com.example.trainers.entities.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TrainerInfoByIdEntityResponse(

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
