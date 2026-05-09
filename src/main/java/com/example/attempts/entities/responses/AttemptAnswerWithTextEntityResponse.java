package com.example.attempts.entities.responses;

import com.example.jooq.generated.enums.AnswerTypeEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record AttemptAnswerWithTextEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        AnswerTypeEnum answerType,

        @Nullable
        Long selectedOptionId,

        @Nullable
        String selectedOptionText,

        @Nullable
        Long selectedErrorItemId,

        @Nullable
        String selectedErrorItemText,

        @Nullable
        String textAnswer
) {}
