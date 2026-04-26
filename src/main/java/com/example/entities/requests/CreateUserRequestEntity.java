package com.example.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record CreateUserRequestEntity(

        @Nonnull
        String email,

        @Nonnull
        String username,

        @Nonnull
        String passwordHash

) {}
