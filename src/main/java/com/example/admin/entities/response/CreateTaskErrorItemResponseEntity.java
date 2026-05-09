package com.example.admin.entities.response;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateTaskErrorItemResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        String fragmentText,

        @Nonnull
        Boolean isError,

        @Nullable
        String explanation

) {}
