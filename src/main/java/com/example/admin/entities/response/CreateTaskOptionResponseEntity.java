package com.example.admin.entities.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record CreateTaskOptionResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        String optionText,

        @Nonnull
        Boolean isCorrect

) {}
