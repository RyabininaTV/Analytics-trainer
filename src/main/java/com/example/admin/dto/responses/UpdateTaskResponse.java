package com.example.admin.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record UpdateTaskResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @JsonProperty(value = "task_type", required = true)
        String taskType,

        @JsonProperty(value = "title", required = true)
        String title,

        @JsonProperty(value = "description", required = true)
        String description,

        @JsonProperty("content")
        String content,

        @JsonProperty(value = "max_score", required = true)
        Integer maxScore,

        @JsonProperty(value = "is_active", required = true)
        Boolean isActive,

        @JsonProperty(value = "auto_check_enabled", required = true)
        Boolean autoCheckEnabled,

        @JsonProperty(value = "created_at", required = true)
        LocalDateTime createdAt,

        @JsonProperty(value = "updated_at", required = true)
        LocalDateTime updatedAt,

        @JsonProperty("options")
        List<UpdateTaskOptionResponse> options,

        @JsonProperty("error_items")
        List<UpdateTaskErrorItemResponse> errorItems

) {

    @Builder
    public record UpdateTaskOptionResponse(

            @JsonProperty(value = "id", required = true)
            Long id,

            @JsonProperty(value = "option_text", required = true)
            String optionText,

            @JsonProperty(value = "is_correct", required = true)
            Boolean isCorrect

    ) {}

    @Builder
    @JsonInclude(NON_NULL)
    public record UpdateTaskErrorItemResponse(

            @JsonProperty(value = "id", required = true)
            Long id,

            @JsonProperty(value = "fragment_text", required = true)
            String fragmentText,

            @JsonProperty(value = "is_error", required = true)
            Boolean isError,

            @JsonProperty("explanation")
            String explanation

    ) {}

}
