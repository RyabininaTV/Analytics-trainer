package com.example.dto.responses;

import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.jooq.generated.enums.UserStatusEnum;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record AuthResponse(

        @Nonnull
        String accessToken,

        @Nonnull
        String refreshToken,

        @Nonnull
        UserResponse user

) {

    @Builder
    public record UserResponse(

            @Nonnull
            Long id,

            @Nonnull
            String email,

            @Nonnull
            String username,

            @Nonnull
            UserRoleEnum role,

            @Nonnull
            UserStatusEnum status

    ) {}

}
