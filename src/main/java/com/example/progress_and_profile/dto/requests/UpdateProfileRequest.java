package com.example.progress_and_profile.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateProfileRequest(

        @JsonProperty(value = "email", required = true)
        @NotBlank(message = "Email is required")
        @Email(message = "Email has invalid format")
        @Size(max = 255, message = "Email must be at most {max} characters")
        String email,

        @JsonProperty(value = "username", required = true)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 100, message = "Username must be from {min} to {max} characters")
        String username,

        @JsonProperty("old_password")
        @Size(min = 8, max = 100, message = "Old password must be from {min} to {max} characters")
        String oldPassword,

        @JsonProperty("new_password")
        @Size(min = 8, max = 100, message = "New password must be from {min} to {max} characters")
        String newPassword

) {}
