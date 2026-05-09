package com.example.attempts.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SubmitAttemptRequest (

    @NotNull(message = "task_id is required")
    @Positive(message = "task_id must be positive")
    @JsonProperty(value = "task_id", required = true)
    Long taskId,

    @NotBlank(message = "answer is required")
    @Size(max = 10000, message = "answer must not exceed {max} characters")
    @JsonProperty(value = "answer", required = true)
    String answer

) {}
