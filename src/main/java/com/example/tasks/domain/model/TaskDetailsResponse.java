package com.example.tasks.domain.model;

import com.example.jooq.generated.enums.TaskTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDetailsResponse(
        @Nonnull
        Long id,

        @JsonProperty(value = "trainer_id", required = true)
        @Nonnull
        Long trainerId,

        @Nonnull
        TaskTypeEnum type,

        @Nonnull
        String title,

        @Nonnull
        String description,

        @Nullable
        String content,

        @Nonnull
        @JsonProperty(value = "max_score", required = true)
        Integer maxScore,

        @Nonnull
        @JsonProperty(value = "auto_check", required = true)
        Boolean autoCheck
) {}
