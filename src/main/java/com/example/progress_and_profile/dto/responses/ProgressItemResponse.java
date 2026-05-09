package com.example.progress_and_profile.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ProgressItemResponse(

        @JsonProperty(value = "trainer_id", required = true)
        Long trainerId,

        @JsonProperty(value = "completed_tasks_count", required = true)
        Integer completedTasksCount,

        @JsonProperty(value = "total_tasks_count", required = true)
        Integer totalTasksCount,

        @JsonProperty(value = "total_score", required = true)
        Integer totalScore,

        @JsonProperty(value = "completion_percent", required = true)
        BigDecimal completionPercent,

        @JsonProperty("last_activity_at")
        LocalDateTime lastActivityAt

) {}
