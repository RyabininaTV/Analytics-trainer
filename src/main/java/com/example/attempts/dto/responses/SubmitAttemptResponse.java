package com.example.attempts.dto.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record SubmitAttemptResponse(

        @Nonnull
        @JsonProperty(value = "task_id", required = true)
        Long taskId,

        @Nonnull
        @JsonProperty(value = "user_id", required = true)
        Long userId,

        @Nonnull
        @JsonProperty(value = "attempt_id", required = true)
        Long attemptId,

        @Nonnull
        @JsonProperty(value = "status", required = true)
        AttemptStatusEnum status,

        @Nullable
        @JsonProperty("score")
        Integer score,

        @Nullable
        @JsonProperty("total_score")
        Integer totalScore
) {}
