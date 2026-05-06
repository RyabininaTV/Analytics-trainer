package com.example.attempts.dto.responses;

import com.example.jooq.generated.enums.AnswerTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record AttemptAnswerDetails(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "answer_type", required = true)
        AnswerTypeEnum answerType,

        @Nullable
        @JsonProperty("selected_option_id")
        Long selectedOptionId,

        @Nullable
        @JsonProperty("selected_option_text")
        String selectedOptionText,

        @Nullable
        @JsonProperty("selected_error_item_id")
        Long selectedErrorItemId,

        @Nullable
        @JsonProperty("selected_error_item_text")
        String selectedErrorItemText,

        @Nullable
        @JsonProperty("text_answer")
        String textAnswer
) {}
