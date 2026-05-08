package com.example.progress_and_profile.entity.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record ExistsUserByEmailAndIdNotEntityRequest(

        @Nonnull
        String email,

        @Nonnull
        Long excludedUserId

) {}
