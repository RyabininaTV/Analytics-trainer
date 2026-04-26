package com.example.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateRevokedTokenRequestEntity(

        @Nonnull
        String tokenId,

        @Nonnull
        LocalDateTime expiresAt

) {}
