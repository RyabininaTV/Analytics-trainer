package com.example.trainers.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UserProgressByTrainerIdResponse(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "user_id", required = true)
        Long userId,

        @Nonnull
        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @Nonnull
        @JsonProperty(value = "completed_tasks_count", required = true)
        Integer completedTasksCount,

        @Nonnull
        @JsonProperty(value = "total_tasks_count", required = true)
        Integer totalTasksCount,

        @Nonnull
        @JsonProperty(value = "total_score", required = true)
        Integer totalScore,

        @Nonnull
        @JsonProperty(value = "completion_percent", required = true)
        BigDecimal completionPercent,

        @Nullable
        @JsonProperty("last_activity_at")
        LocalDateTime lastActivityAt

) {}
