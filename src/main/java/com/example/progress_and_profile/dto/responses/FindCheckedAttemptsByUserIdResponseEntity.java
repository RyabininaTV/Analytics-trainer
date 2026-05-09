package com.example.progress_and_profile.dto.responses;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FindCheckedAttemptsByUserIdResponseEntity(

        @Nonnull
        Long taskId,

        @Nonnull
        LocalDateTime changedAt,

        @Nonnull
        Integer score

) {}
