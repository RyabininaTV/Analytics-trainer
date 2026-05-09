package com.example.attempts.dto.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AttemptResponse(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "task_id", required = true)
        Long taskId,

        @Nonnull
        @JsonProperty(value = "started_at", required = true)
        LocalDateTime startedAt,

        @Nullable
        @JsonProperty("submitted_at")
        LocalDateTime submittedAt,

        @Nonnull
        @JsonProperty(value = "status", required = true)
        AttemptStatusEnum status,

        @Nullable
        @JsonProperty("score")
        Integer score,

        @Nonnull
        @JsonProperty(value = "max_score_snapshot", required = true)
        Integer maxScoreSnapshot,

        @Nullable
        @JsonProperty("is_correct")
        Boolean isCorrect
) {}