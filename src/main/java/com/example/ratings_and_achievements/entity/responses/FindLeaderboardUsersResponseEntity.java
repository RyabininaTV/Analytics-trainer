package com.example.ratings_and_achievements.entity.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FindLeaderboardUsersResponseEntity(

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

        @Nullable
        LocalDateTime lastActivityAt

) {}
