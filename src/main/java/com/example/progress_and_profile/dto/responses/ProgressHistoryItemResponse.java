package com.example.progress_and_profile.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProgressHistoryItemResponse(

        @JsonProperty(value = "changed_at", required = true)
        LocalDateTime changedAt,

        @JsonProperty(value = "total_score", required = true)
        Integer totalScore

) {}
