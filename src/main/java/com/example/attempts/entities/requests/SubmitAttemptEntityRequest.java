package com.example.attempts.entities.requests;

import com.example.jooq.generated.enums.AnswerTypeEnum;
import com.example.jooq.generated.enums.AttemptStatusEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record SubmitAttemptEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        Long taskId,

        @Nonnull
        Integer maxScore,

        @Nonnull
        AttemptStatusEnum status,

        @Nullable
        Integer score,

        @Nullable
        Boolean isCorrect,

        @Nonnull
        AnswerTypeEnum answerType,

        @Nullable
        Long selectedOptionId,

        @Nullable
        Long selectedErrorItemId,

        @Nullable
        String textAnswer
) {}
