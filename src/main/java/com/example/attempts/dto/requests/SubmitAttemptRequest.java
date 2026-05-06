package com.example.attempts.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SubmitAttemptRequest {

    @Nonnull
    @JsonProperty(value = "task_id", required = true)
    private Long taskId;

    @Nullable
    @JsonProperty("user_id")
    private Long userId;

    @Nonnull
    @JsonProperty(value = "answer", required = true)
    private String answer;
}
