package com.example.tasks.entity.requests;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record FindTasksRequestEntity(

        @Nullable
        Long trainerId

) {}
