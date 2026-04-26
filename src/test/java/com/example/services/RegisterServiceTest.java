package com.example.services;

import com.example.dto.requests.RegisterRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.requests.CreateUserRequestEntity;
import com.example.entities.responses.CreateUserResponseEntity;
import com.example.exceptions.EmailIsAlreadyUsedException;
import com.example.exceptions.UsernameIsAlreadyUsedException;
import com.example.repositories.UserRepository;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@mail.ru";
    private static final String RAW_EMAIL = "  TEST@MAIL.RU  ";
    private static final String USERNAME = "test_user";
    private static final String RAW_USERNAME = "  test_user  ";
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
    class RegisterTests {

        @Test
        void emailAndUsernameAreFree_shouldCreateUserAndReturnAuthResponse() {
            mockJwtConfig();

            when(userRepository.existsByEmail(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByUsername(USERNAME))
                    .thenReturn(false);
            when(userRepository.create(any(CreateUserRequestEntity.class)))
                    .thenReturn(createdUser());

            RegisterService registerService = new RegisterService(userRepository, appYaml);

            AuthResponse response = registerService.register(RegisterRequest.builder()
                    .email(RAW_EMAIL)
                    .username(RAW_USERNAME)
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

            ArgumentCaptor<CreateUserRequestEntity> captor =
                    ArgumentCaptor.forClass(CreateUserRequestEntity.class);

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository).existsByUsername(USERNAME);
            verify(userRepository).create(captor.capture());
            verifyNoMoreInteractions(userRepository);

            CreateUserRequestEntity createUserRequest = captor.getValue();

            assertEquals(EMAIL, createUserRequest.email());
            assertEquals(USERNAME, createUserRequest.username());
            assertNotNull(createUserRequest.passwordHash());
            assertNotEquals(PASSWORD, createUserRequest.passwordHash());

            Claims accessClaims = JwtUtil.parseAndValidate(response.accessToken(), appYaml);
            assertEquals(String.valueOf(USER_ID), accessClaims.getSubject());
            assertEquals("access", accessClaims.get("token_type", String.class));
            assertEquals(EMAIL, accessClaims.get("email", String.class));
            assertEquals(USERNAME, accessClaims.get("username", String.class));
            assertEquals(USER.name(), accessClaims.get("role", String.class));

            Claims refreshClaims = JwtUtil.parseAndValidate(response.refreshToken(), appYaml);
            assertEquals(String.valueOf(USER_ID), refreshClaims.getSubject());
            assertEquals("refresh", refreshClaims.get("token_type", String.class));
        }

        @Test
        void emailIsAlreadyUsed_shouldThrowEmailIsAlreadyUsedException() {
            when(userRepository.existsByEmail(EMAIL))
                    .thenReturn(true);

            RegisterService registerService = new RegisterService(userRepository, appYaml);

            assertThrows(
                    EmailIsAlreadyUsedException.class,
                    () -> registerService.register(RegisterRequest.builder()
                            .email(RAW_EMAIL)
                            .username(RAW_USERNAME)
                            .password(PASSWORD)
                            .build()
                    )
            );

            verify(userRepository).existsByEmail(EMAIL);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(appYaml);
        }

        @Test
        void usernameIsAlreadyUsed_shouldThrowUsernameIsAlreadyUsedException() {
            when(userRepository.existsByEmail(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByUsername(USERNAME))
                    .thenReturn(true);

            RegisterService registerService = new RegisterService(userRepository, appYaml);

            assertThrows(
                    UsernameIsAlreadyUsedException.class,
                    () -> registerService.register(RegisterRequest.builder()
                            .email(RAW_EMAIL)
                            .username(RAW_USERNAME)
                            .password(PASSWORD)
                            .build()
                    )
            );

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository).existsByUsername(USERNAME);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(appYaml);
        }
    }

    private void mockJwtConfig() {
        when(appYaml.jwt()).thenReturn(jwt);
        when(jwt.secret()).thenReturn(JWT_SECRET);
        when(jwt.issuer()).thenReturn(JWT_ISSUER);
        lenient().when(jwt.accessTokenTtlMinutes()).thenReturn(ACCESS_TOKEN_TTL_MINUTES);
        lenient().when(jwt.refreshTokenTtlDays()).thenReturn(REFRESH_TOKEN_TTL_DAYS);
    }

    private static CreateUserResponseEntity createdUser() {
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

}
