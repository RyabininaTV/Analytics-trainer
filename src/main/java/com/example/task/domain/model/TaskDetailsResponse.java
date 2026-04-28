package com.example.task.domain.model;

import com.example.jooq.generated.enums.TaskTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record TaskDetailsResponse(

        @Nonnull Long id,

        @JsonProperty(value = "trainer_id", required = true)
        @Nonnull Long trainerId,

        @Nonnull
        TaskTypeEnum type,

        @Nonnull
        String title,

        @Nonnull
        String description,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Nullable String content,

        @Nonnull Integer maxScore,

        @Nonnull Boolean autoCheck
) {}
