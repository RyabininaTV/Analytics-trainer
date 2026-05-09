package com.example.admin.entities.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record DeactivateTaskResponseEntity(

        @Nonnull
        Long id,

        @Nonnull
        Boolean isActive

) {}
