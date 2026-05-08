package com.example.ratings_and_achievements.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record LeaderboardItemByTrainerIdResponse (

    @JsonProperty(value = "position", required = true)
    Integer position,

    @JsonProperty(value = "user_id", required = true)
    Long userId,

    @JsonProperty(value = "username", required = true)
    String username,

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
