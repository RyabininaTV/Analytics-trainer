package com.example.trainers.entities.requests;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record DeleteUserProgressByTrainerIdEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        Long trainerId

) {}
