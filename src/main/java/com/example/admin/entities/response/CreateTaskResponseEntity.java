package com.example.admin.entities.response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateTaskResponseEntity(

        @Nonnull
        Long id,

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
        Boolean autoCheckEnabled,

        @Nonnull
        LocalDateTime createdAt,

        @Nonnull
        LocalDateTime updatedAt

) {}
