package com.example.exception_mapper.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record ErrorResponse(

        @Nonnull
        @JsonProperty(value = "code", required = true)
        String code,

        @Nonnull
        @JsonProperty(value = "message", required = true)
        String message

) {}
