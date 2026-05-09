package com.example.ratings_and_achievements.entity.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FindLeaderboardByTrainerIdResponseEntity(

        @Nonnull
        Long userId,

        @Nonnull
        String username,

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
