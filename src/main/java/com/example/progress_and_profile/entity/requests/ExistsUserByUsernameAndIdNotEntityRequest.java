package com.example.progress_and_profile.entity.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record ExistsUserByUsernameAndIdNotEntityRequest(

        @Nonnull
        String username,

        @Nonnull
        Long excludedUserId

) {}
