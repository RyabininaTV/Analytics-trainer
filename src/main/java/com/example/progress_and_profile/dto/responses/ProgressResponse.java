package com.example.progress_and_profile.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProgressResponse(

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
