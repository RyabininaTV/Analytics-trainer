package com.example.auth.services;

import com.example.auth.dto.requests.LogoutRequest;
import com.example.auth.entities.requests.CreateRevokedTokenRequestEntity;
import com.example.auth.entities.responses.CreateUserResponseEntity;
import com.example.auth.exceptions.InvalidAuthorizationHeaderException;
import com.example.auth.services.LogoutService;
import com.example.repositories.RevokedTokensRepository;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@mail.ru";
    private static final String USERNAME = "test_user";

    private static final String JWT_SECRET = "local-dev-jwt-secret-analytics-trainer-2026-change-me";
    private static final String JWT_ISSUER = "analytics-trainer";
    private static final long ACCESS_TOKEN_TTL_MINUTES = 15L;
    private static final long REFRESH_TOKEN_TTL_DAYS = 14L;

    private static final String BEARER = "Bearer ";

    @Mock
    RevokedTokensRepository revokedTokensRepository;

    @Mock
    AppYamlConfig appYaml;

    @Mock
    AppYamlConfig.Jwt jwt;

    @Mock
    HttpHeaders headers;

    @Nested
    class LogoutTests {

        @Test
        void accessAndRefreshTokensAreValid_shouldRevokeBothTokens() {
            mockJwtConfig();

            CreateUserResponseEntity user = user();

            String accessToken = JwtUtil.createAccessToken(
                    mapToJwtUserClaims(user),
                    appYaml
            );
            String refreshToken = JwtUtil.createRefreshToken(
                    mapToJwtUserClaims(user),
                    appYaml
            );

            when(headers.getHeaderString(AUTHORIZATION))
                    .thenReturn(BEARER + accessToken);

            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken(refreshToken)
                    .build();

            LogoutService logoutService = new LogoutService(revokedTokensRepository, appYaml);

            logoutService.logout(request, headers);

            ArgumentCaptor<CreateRevokedTokenRequestEntity> captor =
                    ArgumentCaptor.forClass(CreateRevokedTokenRequestEntity.class);

            verify(revokedTokensRepository, times(2)).create(captor.capture());
            verifyNoMoreInteractions(revokedTokensRepository);

            List<CreateRevokedTokenRequestEntity> revokedTokens = captor.getAllValues();

            Claims accessClaims = JwtUtil.parseAndValidate(accessToken, appYaml);
            Claims refreshClaims = JwtUtil.parseAndValidate(refreshToken, appYaml);

            assertEquals(accessClaims.getId(), revokedTokens.get(0).tokenId());
            assertEquals(refreshClaims.getId(), revokedTokens.get(1).tokenId());

            assertNotNull(revokedTokens.get(0).expiresAt());
            assertNotNull(revokedTokens.get(1).expiresAt());
            assertTrue(revokedTokens.get(0).expiresAt().isAfter(LocalDateTime.now()));
            assertTrue(revokedTokens.get(1).expiresAt().isAfter(LocalDateTime.now()));

            verify(headers).getHeaderString(AUTHORIZATION);
        }

        @Test
        void refreshTokenIsNull_shouldRevokeOnlyAccessToken() {
            mockJwtConfig();

            String accessToken = JwtUtil.createAccessToken(
                    mapToJwtUserClaims(user()),
                    appYaml
            );

            when(headers.getHeaderString(AUTHORIZATION))
                    .thenReturn(BEARER + accessToken);

            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken(null)
                    .build();

            LogoutService logoutService = new LogoutService(revokedTokensRepository, appYaml);

            logoutService.logout(request, headers);

            ArgumentCaptor<CreateRevokedTokenRequestEntity> captor =
                    ArgumentCaptor.forClass(CreateRevokedTokenRequestEntity.class);

            verify(revokedTokensRepository).create(captor.capture());
            verifyNoMoreInteractions(revokedTokensRepository);

            Claims accessClaims = JwtUtil.parseAndValidate(accessToken, appYaml);

            assertEquals(accessClaims.getId(), captor.getValue().tokenId());
            assertNotNull(captor.getValue().expiresAt());

            verify(headers).getHeaderString(AUTHORIZATION);
        }

        @Test
        void refreshTokenIsBlank_shouldRevokeOnlyAccessToken() {
            mockJwtConfig();

            String accessToken = JwtUtil.createAccessToken(
                    mapToJwtUserClaims(user()),
                    appYaml
            );

            when(headers.getHeaderString(AUTHORIZATION))
                    .thenReturn(BEARER + accessToken);

            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken(" ")
                    .build();

            LogoutService logoutService = new LogoutService(revokedTokensRepository, appYaml);

            logoutService.logout(request, headers);

            verify(revokedTokensRepository, times(1)).create(any(CreateRevokedTokenRequestEntity.class));
            verifyNoMoreInteractions(revokedTokensRepository);

            verify(headers).getHeaderString(AUTHORIZATION);
        }

        @Test
        void authorizationHeaderIsMissing_shouldThrowInvalidAuthorizationHeaderException() {
            when(headers.getHeaderString(AUTHORIZATION))
                    .thenReturn(null);

            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken(null)
                    .build();

            LogoutService logoutService = new LogoutService(revokedTokensRepository, appYaml);

            assertThrows(
                    InvalidAuthorizationHeaderException.class,
                    () -> logoutService.logout(request, headers)
            );

            verify(headers).getHeaderString(AUTHORIZATION);
            verifyNoInteractions(revokedTokensRepository);
            verifyNoInteractions(appYaml);
        }

        @Test
        void authorizationHeaderHasInvalidPrefix_shouldThrowInvalidAuthorizationHeaderException() {
            when(headers.getHeaderString(AUTHORIZATION))
                    .thenReturn("Invalid token");

            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken(null)
                    .build();

            LogoutService logoutService = new LogoutService(revokedTokensRepository, appYaml);

            assertThrows(
                    InvalidAuthorizationHeaderException.class,
                    () -> logoutService.logout(request, headers)
            );

            verify(headers).getHeaderString(AUTHORIZATION);
            verifyNoInteractions(revokedTokensRepository);
            verifyNoInteractions(appYaml);
        }

    }

    private void mockJwtConfig() {
        when(appYaml.jwt()).thenReturn(jwt);
        when(jwt.secret()).thenReturn(JWT_SECRET);
        when(jwt.issuer()).thenReturn(JWT_ISSUER);
        when(jwt.accessTokenTtlMinutes()).thenReturn(ACCESS_TOKEN_TTL_MINUTES);
        lenient().when(jwt.refreshTokenTtlDays())
                .thenReturn(REFRESH_TOKEN_TTL_DAYS);
    }

    private static CreateUserResponseEntity user() {
        return CreateUserResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static JwtUtil.JwtUserClaims mapToJwtUserClaims(@Nonnull CreateUserResponseEntity user) {
        return JwtUtil.JwtUserClaims.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .build();
    }

}
