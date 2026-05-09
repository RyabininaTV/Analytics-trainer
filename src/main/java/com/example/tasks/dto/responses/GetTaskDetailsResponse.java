package com.example.tasks.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record GetTaskDetailsResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @JsonProperty(value = "trainer_title", required = true)
        String trainerTitle,

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

        @JsonProperty(value = "auto_check_enabled", required = true)
        Boolean autoCheckEnabled,

        @JsonProperty(value = "created_at", required = true)
        LocalDateTime createdAt,

        @JsonProperty(value = "updated_at", required = true)
        LocalDateTime updatedAt,

        @JsonProperty("options")
        List<GetTaskOptionResponse> options,

        @JsonProperty("error_items")
        List<GetTaskErrorItemResponse> errorItems

) {

    @Builder
    public record GetTaskOptionResponse(

            @JsonProperty(value = "id", required = true)
            Long id,

            @JsonProperty(value = "option_text", required = true)
            String optionText

    ) {}

    @Builder
    public record GetTaskErrorItemResponse(

            @JsonProperty(value = "id", required = true)
            Long id,

            @JsonProperty(value = "fragment_text", required = true)
            String fragmentText

    ) {}

}
