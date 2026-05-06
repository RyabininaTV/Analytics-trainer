package com.example.attempts.entities.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record AttemptResultEntityResponse(

        @Nonnull
        Long attemptId,

        @Nonnull
        AttemptStatusEnum status,

        @Nullable
        Integer score
) {}
