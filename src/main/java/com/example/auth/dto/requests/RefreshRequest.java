package com.example.auth.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshRequest(

        @NotBlank(message = "Refresh token is required")
        @JsonProperty(value = "refresh_token", required = true)
        String refreshToken

) {}
