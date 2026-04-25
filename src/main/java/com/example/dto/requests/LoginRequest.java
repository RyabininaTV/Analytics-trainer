package com.example.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email has invalid format")
        @Size(max = 255, message = "Email must be at most {max} characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(max = 100, message = "Password must be at most {max} characters")
        String password

) {}
