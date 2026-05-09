package com.example.admin.entities.requests;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateTrainerRequestEntity(

        @Nonnull
        Long id,

        @Nonnull
        String title,

        @Nullable
        String description,

        @Nullable
        String difficultyLevel,

        @Nonnull
        Boolean isActive

) {}
