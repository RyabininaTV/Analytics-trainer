package com.example.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateUserPasswordRequestEntity(

        @Nonnull
        Long userId,

        @Nonnull
        String passwordHash,

        @Nonnull
        LocalDateTime updatedAt

) {}
