package com.example.attempts.dto.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SubmitAttemptResponse {

    @Nonnull
    @JsonProperty(value = "task_id", required = true)
    private Long taskId;

    @Nonnull
    @JsonProperty(value = "user_id", required = true)
    private Long userId;

    @Nonnull
    @JsonProperty(value = "attempt_id", required = true)
    private Long attemptId;

    @Nonnull
    @JsonProperty(value = "status", required = true)
    private AttemptStatusEnum status;

    @Nullable
    @JsonProperty("score")
    private Integer score;

    @Nullable
    @JsonProperty("total_score")
    private Integer totalScore;
}
