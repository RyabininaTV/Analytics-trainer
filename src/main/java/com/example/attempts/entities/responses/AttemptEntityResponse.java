package com.example.attempts.entities.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttemptEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        Long userId,

        @Nonnull
        Long taskId,

        @Nonnull
        LocalDateTime startedAt,

        @Nullable
        LocalDateTime submittedAt,

        @Nonnull
        AttemptStatusEnum status,

        @Nullable
        Integer score,

        @Nonnull
        Integer maxScoreSnapshot,

        @Nullable
        Boolean isCorrect
) {}