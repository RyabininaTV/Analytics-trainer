package com.example.admin.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record CreateTaskOptionRequestEntity(

        @Nonnull
        Long taskId,

        @Nonnull
        String optionText,

        @Nonnull
        Boolean isCorrect

) {}
