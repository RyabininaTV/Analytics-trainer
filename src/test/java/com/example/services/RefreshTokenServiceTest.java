package com.example.services;

import com.example.dto.requests.RefreshRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.responses.CreateUserResponseEntity;
import com.example.entities.responses.FindUserByIdResponseEntity;
import com.example.exceptions.InvalidRefreshTokenException;
import com.example.exceptions.RefreshTokenIsRevokedException;
import com.example.exceptions.UserIsBlockedException;
import com.example.repositories.RevokedTokenRepository;
import com.example.repositories.UserRepository;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static com.example.jooq.generated.enums.UserStatusEnum.BLOCKED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@mail.ru";
    private static final String USERNAME = "test_user";

    private static final String JWT_SECRET = "local-dev-jwt-secret-analytics-trainer-2026-change-me";
    private static final String JWT_ISSUER = "analytics-trainer";
    private static final long ACCESS_TOKEN_TTL_MINUTES = 15L;
    private static final long REFRESH_TOKEN_TTL_DAYS = 14L;

    @Mock
    UserRepository userRepository;

    @Mock
    RevokedTokenRepository revokedTokenRepository;

    @Mock
    AppYamlConfig appYaml;

    @Mock
    AppYamlConfig.Jwt jwt;

    @Nested
    class RefreshTests {

        @Test
        void refreshTokenIsValid_shouldReturnNewAuthResponse() {
            mockJwtConfig();

            String refreshToken = JwtUtil.createRefreshToken(
                    mapToJwtUserClaims(userForToken()),
                    appYaml
            );

            Claims refreshClaims = JwtUtil.parseAndValidate(refreshToken, appYaml);

            when(revokedTokenRepository.existsActiveByTokenId(refreshClaims.getId()))
                    .thenReturn(false);
            when(userRepository.findById(USER_ID))
                    .thenReturn(Optional.of(activeUser()));

            RefreshTokenService refreshTokenService = new RefreshTokenService(
                    userRepository,
                    revokedTokenRepository,
                    appYaml
            );

            AuthResponse response = refreshTokenService.refresh(RefreshRequest.builder()
                    .refreshToken(refreshToken)
                    .build()
            );

            assertNotNull(response);
            assertNotNull(response.accessToken());
            assertNotNull(response.refreshToken());

            assertNotNull(response.user());
            assertEquals(USER_ID, response.user().id());
            assertEquals(EMAIL, response.user().email());
            assertEquals(USERNAME, response.user().username());
            assertEquals(USER, response.user().role());
            assertEquals(ACTIVE, response.user().status());

            Claims accessClaims = JwtUtil.parseAndValidate(response.accessToken(), appYaml);
            assertEquals(String.valueOf(USER_ID), accessClaims.getSubject());
            assertEquals("access", accessClaims.get("token_type", String.class));
            assertEquals(EMAIL, accessClaims.get("email", String.class));
            assertEquals(USERNAME, accessClaims.get("username", String.class));
            assertEquals(USER.name(), accessClaims.get("role", String.class));

            Claims newRefreshClaims = JwtUtil.parseAndValidate(response.refreshToken(), appYaml);
            assertEquals(String.valueOf(USER_ID), newRefreshClaims.getSubject());
            assertEquals("refresh", newRefreshClaims.get("token_type", String.class));

            verify(revokedTokenRepository).existsActiveByTokenId(refreshClaims.getId());
            verify(userRepository).findById(USER_ID);
            verifyNoMoreInteractions(revokedTokenRepository, userRepository);
        }

        @Test
        void tokenIsAccessToken_shouldThrowInvalidRefreshTokenException() {
            mockJwtConfig();

            String accessToken = JwtUtil.createAccessToken(
                    mapToJwtUserClaims(userForToken()),
                    appYaml
            );

            RefreshTokenService refreshTokenService = new RefreshTokenService(
                    userRepository,
                    revokedTokenRepository,
                    appYaml
            );

            assertThrows(
                    InvalidRefreshTokenException.class,
                    () -> refreshTokenService.refresh(RefreshRequest.builder()
                            .refreshToken(accessToken)
                            .build()
                    )
            );

            verifyNoInteractions(revokedTokenRepository, userRepository);
        }

        @Test
        void refreshTokenIsRevoked_shouldThrowRefreshTokenIsRevokedException() {
            mockJwtConfig();

            String refreshToken = JwtUtil.createRefreshToken(
                    mapToJwtUserClaims(userForToken()),
                    appYaml
            );

            Claims refreshClaims = JwtUtil.parseAndValidate(refreshToken, appYaml);

            when(revokedTokenRepository.existsActiveByTokenId(refreshClaims.getId()))
                    .thenReturn(true);

            RefreshTokenService refreshTokenService = new RefreshTokenService(
                    userRepository,
                    revokedTokenRepository,
                    appYaml
            );

            assertThrows(
                    RefreshTokenIsRevokedException.class,
                    () -> refreshTokenService.refresh(RefreshRequest.builder()
                            .refreshToken(refreshToken)
                            .build()
                    )
            );

            verify(revokedTokenRepository).existsActiveByTokenId(refreshClaims.getId());
            verifyNoInteractions(userRepository);
            verifyNoMoreInteractions(revokedTokenRepository);
        }

        @Test
        void userDoesNotExist_shouldThrowInvalidRefreshTokenException() {
            mockJwtConfig();

            String refreshToken = JwtUtil.createRefreshToken(
                    mapToJwtUserClaims(userForToken()),
                    appYaml
            );

            Claims refreshClaims = JwtUtil.parseAndValidate(refreshToken, appYaml);

            when(revokedTokenRepository.existsActiveByTokenId(refreshClaims.getId()))
                    .thenReturn(false);
            when(userRepository.findById(USER_ID))
                    .thenReturn(Optional.empty());

            RefreshTokenService refreshTokenService = new RefreshTokenService(
                    userRepository,
                    revokedTokenRepository,
                    appYaml
            );

            assertThrows(
                    InvalidRefreshTokenException.class,
                    () -> refreshTokenService.refresh(
                            RefreshRequest.builder()
                                    .refreshToken(refreshToken)
                                    .build()
                    )
            );

            verify(revokedTokenRepository).existsActiveByTokenId(refreshClaims.getId());
            verify(userRepository).findById(USER_ID);
            verifyNoMoreInteractions(revokedTokenRepository, userRepository);
        }

        @Test
        void userIsBlocked_shouldThrowUserIsBlockedException() {
            mockJwtConfig();

            String refreshToken = JwtUtil.createRefreshToken(
                    mapToJwtUserClaims(userForToken()),
                    appYaml
            );

            Claims refreshClaims = JwtUtil.parseAndValidate(refreshToken, appYaml);

            when(revokedTokenRepository.existsActiveByTokenId(refreshClaims.getId()))
                    .thenReturn(false);
            when(userRepository.findById(USER_ID))
                    .thenReturn(Optional.of(blockedUser()));

            RefreshTokenService refreshTokenService = new RefreshTokenService(
                    userRepository,
                    revokedTokenRepository,
                    appYaml
            );

            assertThrows(
                    UserIsBlockedException.class,
                    () -> refreshTokenService.refresh(RefreshRequest.builder()
                            .refreshToken(refreshToken)
                            .build()
                    )
            );

            verify(revokedTokenRepository).existsActiveByTokenId(refreshClaims.getId());
            verify(userRepository).findById(USER_ID);
            verifyNoMoreInteractions(revokedTokenRepository, userRepository);
        }
    }

    private void mockJwtConfig() {
        when(appYaml.jwt()).thenReturn(jwt);
        when(jwt.secret()).thenReturn(JWT_SECRET);
        when(jwt.issuer()).thenReturn(JWT_ISSUER);
        lenient().when(jwt.accessTokenTtlMinutes()).thenReturn(ACCESS_TOKEN_TTL_MINUTES);
        lenient().when(jwt.refreshTokenTtlDays()).thenReturn(REFRESH_TOKEN_TTL_DAYS);
    }

    private static CreateUserResponseEntity userForToken() {
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

    private static FindUserByIdResponseEntity activeUser() {
        return FindUserByIdResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static FindUserByIdResponseEntity blockedUser() {
        return FindUserByIdResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .status(BLOCKED)
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
