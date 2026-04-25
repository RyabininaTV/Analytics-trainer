package com.example.services;

import com.example.dto.requests.LogoutRequest;
import com.example.entities.requests.CreateRevokedTokenRequestEntity;
import com.example.exceptions.InvalidAuthorizationHeaderException;
import com.example.repositories.RevokedTokenRepository;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LogoutService {

    private static final String BEARER_PREFIX = "Bearer ";

    RevokedTokenRepository revokedTokenRepository;

    AppYamlConfig appYaml;

    @Transactional
    public void logout(
            @Nonnull LogoutRequest request,
            HttpHeaders headers
    ) {
        String accessToken = extractBearerToken(headers);
        Claims accessClaims = JwtUtil.parseAndValidate(accessToken, appYaml);

        revokedTokenRepository.create(CreateRevokedTokenRequestEntity.builder()
                .tokenId(accessClaims.getId())
                .expiresAt(mapToLocalDateTime(accessClaims.getExpiration()))
                .build()
        );

        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            return;
        }

        Claims refreshClaims = JwtUtil.parseAndValidate(request.refreshToken(), appYaml);

        revokedTokenRepository.create(CreateRevokedTokenRequestEntity.builder()
                .tokenId(refreshClaims.getId())
                .expiresAt(mapToLocalDateTime(refreshClaims.getExpiration()))
                .build()
        );
    }

    @Nonnull
    private static String extractBearerToken(@Nonnull HttpHeaders headers) {
        String authHeader = headers.getHeaderString(AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidAuthorizationHeaderException();
        }

        return authHeader.substring(BEARER_PREFIX.length());
    }

    @Nonnull
    private static LocalDateTime mapToLocalDateTime(@Nonnull Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

}
