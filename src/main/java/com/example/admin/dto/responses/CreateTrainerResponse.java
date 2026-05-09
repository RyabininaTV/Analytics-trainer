package com.example.admin.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record CreateTrainerResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "title", required = true)
        String title,

        @JsonProperty("description")
        String description,

        @JsonProperty("difficulty_level")
        String difficultyLevel,

        @JsonProperty(value = "is_active", required = true)
        Boolean isActive,

        @JsonProperty(value = "created_at", required = true)
        LocalDateTime createdAt,

        @JsonProperty(value = "updated_at", required = true)
        LocalDateTime updatedAt

) {}
