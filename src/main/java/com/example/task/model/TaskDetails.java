package com.example.task.model;

import com.example.jooq.generated.enums.TaskTypeEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder(toBuilder = true)
public record TaskDetails(

        long id,

        long trainerId,

        @Nonnull
        TaskTypeEnum type,

        @Nonnull
        String title,

        @Nonnull
        String description,

        @Nullable
        String content,

        int maxScore,

        boolean autoCheck
) {
}
