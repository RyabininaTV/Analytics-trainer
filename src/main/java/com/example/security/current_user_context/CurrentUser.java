package com.example.security.current_user_context;

import com.example.jooq.generated.enums.UserRoleEnum;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record CurrentUser(

        @Nonnull
        Long id,

        @Nonnull
        String email,

        @Nonnull
        String username,

        @Nonnull
        UserRoleEnum role

) {}
