package com.example.attempts.entities.responses;

import com.example.jooq.generated.enums.AnswerTypeEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record AttemptAnswerEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        Long attemptId,

        @Nonnull
        AnswerTypeEnum answerType,

        @Nullable
        Long selectedOptionId,

        @Nullable
        Long selectedErrorItemId,

        @Nullable
        String textAnswer
) {}
