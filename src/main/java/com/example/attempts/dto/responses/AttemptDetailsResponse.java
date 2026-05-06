package com.example.attempts.dto.responses;

import com.example.jooq.generated.enums.AttemptStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AttemptDetailsResponse(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "user_id", required = true)
        Long userId,

        @Nonnull
        @JsonProperty(value = "task_id", required = true)
        Long taskId,

        @Nonnull
        @JsonProperty(value = "started_at", required = true)
        LocalDateTime startedAt,

        @Nullable
        @JsonProperty("submitted_at")
        LocalDateTime submittedAt,

        @Nonnull
        @JsonProperty(value = "status", required = true)
        AttemptStatusEnum status,

        @Nullable
        @JsonProperty("score")
        Integer score,

        @Nonnull
        @JsonProperty(value = "max_score_snapshot", required = true)
        Integer maxScoreSnapshot,

        @Nullable
        @JsonProperty("is_correct")
        Boolean isCorrect,

        @Nullable
        @JsonProperty("auto_checked")
        Boolean autoChecked,

        @Nullable
        @JsonProperty("needs_manual_review")
        Boolean needsManualReview,

        @Nullable
        @JsonProperty("reviewer_comment")
        String reviewerComment,

        @Nullable
        @JsonProperty("reviewed_at")
        LocalDateTime reviewedAt,

        @Nonnull
        @JsonProperty(value = "answers", required = true)
        List<AttemptAnswerDetails> answers
) {}
