package com.example.admin.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateTrainerRequest(

        @NotBlank(message = "Title is required")
        @JsonProperty(value = "title", required = true)
        String title,

        @JsonProperty("description")
        String description,

        @JsonProperty("difficulty_level")
        @Pattern(
                regexp = "EASY|MEDIUM|HARD",
                message = "Уровень сложности должен быть одним из значений: EASY, MEDIUM, HARD"
        )
        String difficultyLevel,

        @NotBlank(message = "IsActive is required")
        @JsonProperty(value = "is_active", required = true)
        Boolean isActive

) {}

