package com.example.auth.dto.responses;

import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record AuthResponse(

        @Nonnull
        @JsonProperty(value = "access_token", required = true)
        String accessToken,

        @Nonnull
        @JsonProperty(value = "refresh_token", required = true)
        String refreshToken,

        @Nonnull
        @JsonProperty(value = "user", required = true)
        UserResponse user

) {

    @Builder
    public record UserResponse(

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
            UserStatusEnum status

    ) {}

}
