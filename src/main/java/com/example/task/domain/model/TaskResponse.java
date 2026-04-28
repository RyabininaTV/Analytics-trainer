package com.example.task.domain.model;

import com.example.jooq.generated.enums.TaskTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record TaskResponse(

        @Nonnull Long id,

        @Nonnull
        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @Nonnull TaskTypeEnum type,

        @Nonnull String title
) {}
