package com.example.attempts.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record GetAttemptDetailsEntityRequest(

        @Nonnull
        Long attemptId,

        @Nonnull
        Long userId
) {}
