package com.example.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email has invalid format")
        @Size(max = 255, message = "Email must be at most {max} characters")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be from {min} to {max} characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be from {min} to {max} characters")
        String password

) {}
