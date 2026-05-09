package com.example.progress_and_profile.entity.responses;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FindUserProgressByUserIdResponseEntity(

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
