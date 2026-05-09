package com.example.progress_and_profile.entity.requests;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateProfileEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        String email,

        @Nonnull
        String username,

        @Nonnull
        String passwordHash,

        @Nonnull
        LocalDateTime updatedAt

) {}
