package com.example.admin.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DeleteTaskResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "is_active", required = true)
        Boolean isActive

) {}
