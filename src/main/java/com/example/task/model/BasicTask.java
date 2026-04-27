package com.example.task.model;

import com.example.jooq.generated.enums.TaskTypeEnum;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record BasicTask(
        long id,
        long trainerId,
        @Nonnull
        TaskTypeEnum type,
        @Nonnull
        String title
) { }
