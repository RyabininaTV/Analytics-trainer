package com.example.tasks.entity.responses;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record FindTaskOptionsByTaskIdResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        String optionText

) {}
