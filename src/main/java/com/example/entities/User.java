package com.example.entities;

import lombok.Builder;
import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

@Builder
public record User (
        @Nullable
        Long id,

        @Nonnull
        String email,

        @Nonnull
        String username,

        @Nonnull
        String passwordHash,

        @Nonnull
        UserRoleEnum role,

        @Nonnull
        UserStatusEnum status,

        @Nullable
        LocalDateTime createdAt,

        @Nullable
        LocalDateTime updatedAt
) {}
