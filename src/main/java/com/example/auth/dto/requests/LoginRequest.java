package com.example.auth.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email has invalid format")
        @JsonProperty(value = "email", required = true)
        @Size(max = 255, message = "Email must be at most {max} characters")
        String email,

        @NotBlank(message = "Password is required")
        @JsonProperty(value = "password", required = true)
        @Size(max = 100, message = "Password must be at most {max} characters")
        String password

) {}
