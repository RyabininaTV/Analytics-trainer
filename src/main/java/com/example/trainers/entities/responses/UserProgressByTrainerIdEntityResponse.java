package com.example.trainers.entities.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UserProgressByTrainerIdEntityResponse (

        @Nonnull
        Long id,

        @Nonnull
        Long userId,

        @Nonnull
        Long trainerId,

        @Nonnull
        Integer completedTasksCount,

        @Nonnull
        Integer totalTasksCount,

        @Nonnull
        Integer totalScore,

        @Nonnull
        BigDecimal completionPercent,

        @Nullable
        LocalDateTime lastActivityAt

) {}
