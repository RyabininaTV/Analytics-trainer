package com.example.attempts.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateUserProgressEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        Long trainerId,

        @Nonnull
        Integer totalScore,

        @Nonnull
        Integer completedTasksCount,

        @Nonnull
        Integer totalTasksCount,

        @Nonnull
        BigDecimal completionPercent
) {}

