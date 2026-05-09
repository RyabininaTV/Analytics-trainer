package com.example.tasks.entity.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FindTasksResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        Long trainerId,

        @Nonnull
        String trainerTitle,

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
        Boolean autoCheckEnabled,

        @Nonnull
        LocalDateTime createdAt,

        @Nonnull
        LocalDateTime updatedAt

) {}
