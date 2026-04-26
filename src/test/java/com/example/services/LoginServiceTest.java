package com.example.services;

import com.example.dto.requests.LoginRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.responses.FindUserByEmailResponseEntity;
import com.example.exceptions.InvalidEmailOrPasswordException;
import com.example.exceptions.UserIsBlockedException;
import com.example.repositories.UserRepository;
import com.example.services.LoginService;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import io.quarkus.elytron.security.common.BcryptUtil;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.wildfly.common.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@mail.ru";
    private static final String RAW_EMAIL = "  TEST@MAIL.RU  ";
    private static final String USERNAME = "test_user";
    private static final String PASSWORD = "password123";

    private static final String JWT_SECRET = "local-dev-jwt-secret-analytics-trainer-2026-change-me";
    private static final String JWT_ISSUER = "analytics-trainer";
    private static final long ACCESS_TOKEN_TTL_MINUTES = 15L;
    private static final long REFRESH_TOKEN_TTL_DAYS = 14L;

    @Mock
    UserRepository userRepository;

    @Mock
    AppYamlConfig appYaml;

    @Mock
    AppYamlConfig.Jwt jwt;

    @Nested
    class LoginTests {

        @Test
        void userExistsAndPasswordIsCorrect_shouldReturnAuthResponse() {
            mockJwtConfig();

            String passwordHash = BcryptUtil.bcryptHash(PASSWORD);

            FindUserByEmailResponseEntity user = activeUser(passwordHash);

            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(user));

            LoginService loginService = new LoginService(userRepository, appYaml);

            AuthResponse response = loginService.login(LoginRequest.builder()
                    .email(RAW_EMAIL)
                    .password(PASSWORD)
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
            assertEquals(EMAIL, accessClaims.get("email", String.class));
            assertEquals(USERNAME, accessClaims.get("username", String.class));
            assertEquals(USER.name(), accessClaims.get("role", String.class));
            assertEquals("access", accessClaims.get("token_type", String.class));

            Claims refreshClaims = JwtUtil.parseAndValidate(response.refreshToken(), appYaml);
            assertEquals(String.valueOf(USER_ID), refreshClaims.getSubject());
            assertEquals("refresh", refreshClaims.get("token_type", String.class));

            verify(userRepository).findByEmail(EMAIL);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void userDoesNotExist_shouldThrowInvalidEmailOrPasswordException() {
            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.empty());

            LoginService loginService = new LoginService(userRepository, appYaml);

            assertThrows(
                    InvalidEmailOrPasswordException.class,
                    () -> loginService.login(LoginRequest.builder()
                            .email(RAW_EMAIL)
                            .password(PASSWORD)
                            .build()
                    )
            );

            verify(userRepository).findByEmail(EMAIL);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(appYaml);
        }

        @Test
        void passwordIsIncorrect_shouldThrowInvalidEmailOrPasswordException() {
            String passwordHash = BcryptUtil.bcryptHash(PASSWORD);

            FindUserByEmailResponseEntity user = activeUser(passwordHash);

            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(user));

            LoginService loginService = new LoginService(userRepository, appYaml);

            assertThrows(
                    InvalidEmailOrPasswordException.class,
                    () -> loginService.login(LoginRequest.builder()
                            .email(RAW_EMAIL)
                            .password("wrong-password")
                            .build()
                    )
            );

            verify(userRepository).findByEmail(EMAIL);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(appYaml);
        }

        @Test
        void userIsBlocked_shouldThrowUserIsBlockedException() {
            String passwordHash = BcryptUtil.bcryptHash(PASSWORD);

            FindUserByEmailResponseEntity user = blockedUser(passwordHash);

            when(userRepository.findByEmail(EMAIL))
                    .thenReturn(Optional.of(user));

            LoginService loginService = new LoginService(userRepository, appYaml);

            assertThrows(
                    UserIsBlockedException.class,
                    () -> loginService.login(LoginRequest.builder()
                            .email(RAW_EMAIL)
                            .password(PASSWORD)
                            .build()
                    )
            );

            verify(userRepository).findByEmail(EMAIL);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(appYaml);
        }

    }

    private void mockJwtConfig() {
        when(appYaml.jwt()).thenReturn(jwt);
        when(jwt.secret()).thenReturn(JWT_SECRET);
        when(jwt.issuer()).thenReturn(JWT_ISSUER);
        when(jwt.accessTokenTtlMinutes()).thenReturn(ACCESS_TOKEN_TTL_MINUTES);
        when(jwt.refreshTokenTtlDays()).thenReturn(REFRESH_TOKEN_TTL_DAYS);
    }

    private static FindUserByEmailResponseEntity activeUser(String passwordHash) {
        return FindUserByEmailResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .passwordHash(passwordHash)
                .role(USER)
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static FindUserByEmailResponseEntity blockedUser(String passwordHash) {
        return FindUserByEmailResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .passwordHash(passwordHash)
                .role(USER)
                .status(BLOCKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
