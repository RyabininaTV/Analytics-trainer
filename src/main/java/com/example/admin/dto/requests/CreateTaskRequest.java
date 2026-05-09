package com.example.admin.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateTaskRequest(

        @JsonProperty(value = "trainer_id", required = true)
        @NotNull(message = "Идентификатор тренажёра обязателен")
        @Positive(message = "Идентификатор тренажёра должен быть положительным")
        Long trainerId,

        @JsonProperty(value = "task_type", required = true)
        @NotBlank(message = "Тип задания обязателен")
        @Pattern(
                regexp = "TEST|ERROR_FIND|OPEN",
                message = "Тип задания должен быть одним из значений: TEST, ERROR_FIND, OPEN"
        )
        String taskType,

        @JsonProperty(value = "title", required = true)
        @NotBlank(message = "Название задания не должно быть пустым")
        @Size(max = 255, message = "Название задания не должно быть длиннее 255 символов")
        String title,

        @JsonProperty(value = "description", required = true)
        @NotBlank(message = "Описание задания не должно быть пустым")
        String description,

        @JsonProperty("content")
        String content,

        @JsonProperty(value = "max_score", required = true)
        @NotNull(message = "Максимальный балл обязателен")
        @Min(value = 0, message = "Максимальный балл не может быть отрицательным")
        Integer maxScore,

        @JsonProperty(value = "is_active", required = true)
        @NotNull(message = "Признак активности задания обязателен")
        Boolean isActive,

        @JsonProperty(value = "auto_check_enabled", required = true)
        @NotNull(message = "Признак автоматической проверки обязателен")
        Boolean autoCheckEnabled,

        @JsonProperty("options")
        List<CreateTaskOptionRequest> options,

        @JsonProperty("error_items")
        List<CreateTaskErrorItemRequest> errorItems

) {

    @Builder
    public record CreateTaskOptionRequest(

            @JsonProperty(value = "option_text", required = true)
            @NotBlank(message = "Текст варианта ответа не должен быть пустым")
            String optionText,

            @JsonProperty(value = "is_correct", required = true)
            @NotNull(message = "Признак правильности варианта ответа обязателен")
            Boolean isCorrect

    ) {}

    @Builder
    public record CreateTaskErrorItemRequest(

            @JsonProperty(value = "fragment_text", required = true)
            @NotBlank(message = "Текст фрагмента не должен быть пустым")
            String fragmentText,

            @JsonProperty(value = "is_error", required = true)
            @NotNull(message = "Признак ошибки во фрагменте обязателен")
            Boolean isError,

            @JsonProperty("explanation")
            String explanation

    ) {}

}
