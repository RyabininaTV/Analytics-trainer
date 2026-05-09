package com.example.tasks.entity.responses;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record FindTaskErrorItemsByTaskIdResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        String fragmentText

) {}
