package com.example.entities.responses;

import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateUserResponseEntity(

        @Nonnull
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

        @Nonnull
        LocalDateTime createdAt,

        @Nonnull
        LocalDateTime updatedAt

) {}
