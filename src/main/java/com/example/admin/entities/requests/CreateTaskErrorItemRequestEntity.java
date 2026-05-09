package com.example.admin.entities.requests;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateTaskErrorItemRequestEntity(

        @Nonnull
        Long taskId,

        @Nonnull
        String fragmentText,

        @Nonnull
        Boolean isError,

        @Nullable
        String explanation

) {}
