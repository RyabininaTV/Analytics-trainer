package com.example.trainers.entities.responses;

import com.example.jooq.generated.enums.TaskTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record TaskByTrainerIdEntityResponse(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @Nonnull
        @JsonProperty(value = "task_type", required = true)
        TaskTypeEnum taskType,

        @Nonnull
        @JsonProperty(value = "title", required = true)
        String title,

        @Nonnull
        @JsonProperty(value = "description", required = true)
        String description,

        @Nullable
        @JsonProperty("content")
        String content,

        @Nonnull
        @JsonProperty(value = "max_score", required = true)
        Integer maxScore,

        @Nonnull
        @JsonProperty(value = "is_active", required = true)
        Boolean isActive,

        @Nonnull
        @JsonProperty(value = "auto_check_enabled", required = true)
        Boolean autoCheckEnabled,

        @Nonnull
        @JsonProperty(value = "created_at", required = true)
        LocalDateTime createdAt,

        @Nonnull
        @JsonProperty(value = "updated_at", required = true)
        LocalDateTime updatedAt

) {}
