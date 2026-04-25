package com.example.dto.requests;

import lombok.Builder;

@Builder
public record LogoutRequest(

        String refreshToken

) {}
