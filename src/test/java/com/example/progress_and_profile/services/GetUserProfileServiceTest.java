package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProfileResponse;
import com.example.progress_and_profile.entity.responses.GetProfileResponseEntity;
import com.example.progress_and_profile.exceptions.ProfileIsNotActiveException;
import com.example.progress_and_profile.exceptions.ProfileNotFoundException;
import com.example.repositories.UserRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static com.example.jooq.generated.enums.UserStatusEnum.BLOCKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileServiceTest {

    private static final Long USER_ID = 10L;

    private static final String EMAIL = "user@test.com";
    private static final String USERNAME = "test-user";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    GetUserProfileService getUserProfileService;

    @Test
    void getUserProfile_shouldReturnUserProfile() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.getProfileById(USER_ID))
                .thenReturn(Optional.of(activeProfileEntity()));

        ProfileResponse response = getUserProfileService.getUserProfile();

        assertEquals(USER_ID, response.id());
        assertEquals(EMAIL, response.email());
        assertEquals(USERNAME, response.username());
        assertEquals(USER, response.role());
        assertEquals(ACTIVE, response.status());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());

        verify(userRepository).getProfileById(USER_ID);
    }

    @Test
    void getUserProfile_shouldThrowProfileNotFoundException_whenProfileDoesNotExist() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.getProfileById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ProfileNotFoundException.class,
                () -> getUserProfileService.getUserProfile()
        );

        verify(userRepository).getProfileById(USER_ID);
    }

    @Test
    void getUserProfile_shouldThrowProfileIsNotActiveException_whenProfileIsNotActive() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.getProfileById(USER_ID))
                .thenReturn(Optional.of(blockedProfileEntity()));

        assertThrows(
                ProfileIsNotActiveException.class,
                () -> getUserProfileService.getUserProfile()
        );

        verify(userRepository).getProfileById(USER_ID);
    }

    private static GetProfileResponseEntity activeProfileEntity() {
        return GetProfileResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .status(ACTIVE)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static GetProfileResponseEntity blockedProfileEntity() {
        return GetProfileResponseEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .status(BLOCKED)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
