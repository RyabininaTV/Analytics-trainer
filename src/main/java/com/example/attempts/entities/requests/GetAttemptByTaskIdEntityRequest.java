package com.example.attempts.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record GetAttemptByTaskIdEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        Long taskId
) {}
