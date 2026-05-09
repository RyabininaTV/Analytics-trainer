package com.example.admin.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateTrainerRequest(

        @JsonProperty(value = "title", required = true)
        @NotBlank(message = "Название тренажёра не должно быть пустым")
        @Size(max = 255, message = "Название тренажёра не должно быть длиннее {max} символов")
        String title,

        @JsonProperty("description")
        String description,

        @JsonProperty("difficulty_level")
        @Pattern(
                regexp = "EASY|MEDIUM|HARD",
                message = "Уровень сложности должен быть одним из значений: EASY, MEDIUM, HARD"
        )
        String difficultyLevel,

        @JsonProperty(value = "is_active", required = true)
        @NotNull(message = "Признак активности тренажёра обязателен")
        Boolean isActive

) {}
