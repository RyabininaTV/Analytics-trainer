package com.example.progress_and_profile.dto.responses;

import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileResponse(

        @Nonnull
        @JsonProperty(value = "id", required = true)
        Long id,

        @Nonnull
        @JsonProperty(value = "email", required = true)
        String email,

        @Nonnull
        @JsonProperty(value = "username", required = true)
        String username,

        @Nonnull
        @JsonProperty(value = "role", required = true)
        UserRoleEnum role,

        @Nonnull
        @JsonProperty(value = "status", required = true)
        UserStatusEnum status,

        @Nonnull
        @JsonProperty(value = "created_at", required = true)
        LocalDateTime createdAt,

        @Nonnull
        @JsonProperty(value = "updated_at", required = true)
        LocalDateTime updatedAt

) {}
