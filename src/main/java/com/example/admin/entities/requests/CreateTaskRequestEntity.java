package com.example.admin.entities.requests;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateTaskRequestEntity(

        @Nonnull
        Long trainerId,

        @Nonnull
        String taskType,

        @Nonnull
        String title,

        @Nonnull
        String description,

        @Nullable
        String content,

        @Nonnull
        Integer maxScore,

        @Nonnull
        Boolean isActive,

        @Nonnull
        Boolean autoCheckEnabled

) {}
