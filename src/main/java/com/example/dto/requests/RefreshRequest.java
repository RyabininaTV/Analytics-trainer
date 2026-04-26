package com.example.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshRequest(

        @NotBlank(message = "Refresh token is required")
        @JsonProperty(defaultValue = "refresh_token", required = true)
        String refreshToken

) {}
