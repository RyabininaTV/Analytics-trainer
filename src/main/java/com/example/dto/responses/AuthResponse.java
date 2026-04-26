package com.example.dto.responses;

import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record AuthResponse(

        @Nonnull
        @JsonProperty(defaultValue = "access_token", required = true)
        String accessToken,

        @Nonnull
        @JsonProperty(defaultValue = "refresh_token", required = true)
        String refreshToken,

        @Nonnull
        @JsonProperty(defaultValue = "user", required = true)
        UserResponse user

) {

    @Builder
    public record UserResponse(

            @Nonnull
            @JsonProperty(defaultValue = "id", required = true)
            Long id,

            @Nonnull
            @JsonProperty(defaultValue = "email", required = true)
            String email,

            @Nonnull
            @JsonProperty(defaultValue = "username", required = true)
            String username,

            @Nonnull
            @JsonProperty(defaultValue = "role", required = true)
            UserRoleEnum role,

            @Nonnull
            @JsonProperty(defaultValue = "status", required = true)
            UserStatusEnum status

    ) {}

}
