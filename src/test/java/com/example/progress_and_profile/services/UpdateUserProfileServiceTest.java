package com.example.progress_and_profile.services;

import com.example.auth.exceptions.EmailIsAlreadyUsedException;
import com.example.auth.exceptions.UsernameIsAlreadyUsedException;
import com.example.progress_and_profile.dto.requests.UpdateProfileRequest;
import com.example.progress_and_profile.dto.responses.UpdateProfileResponse;
import com.example.progress_and_profile.entity.requests.ExistsUserByEmailAndIdNotEntityRequest;
import com.example.progress_and_profile.entity.requests.ExistsUserByUsernameAndIdNotEntityRequest;
import com.example.progress_and_profile.entity.requests.UpdateProfileEntityRequest;
import com.example.progress_and_profile.entity.responses.FindProfileForUpdateByIdEntityResponse;
import com.example.progress_and_profile.entity.responses.UpdateProfileEntityResponse;
import com.example.progress_and_profile.exceptions.InvalidOldPasswordException;
import com.example.progress_and_profile.exceptions.PasswordChangeDataIsIncompleteException;
import com.example.progress_and_profile.exceptions.ProfileIsNotActiveException;
import com.example.progress_and_profile.exceptions.ProfileNotFoundException;
import com.example.repositories.UserRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static com.example.jooq.generated.enums.UserStatusEnum.BLOCKED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileServiceTest {

    private static final Long USER_ID = 10L;

    private static final String RAW_EMAIL = "  USER@TEST.COM  ";
    private static final String NORMALIZED_EMAIL = "user@test.com";

    private static final String RAW_USERNAME = "  test-user  ";
    private static final String TRIMMED_USERNAME = "test-user";

    private static final String OLD_PASSWORD = "old-password";
    private static final String NEW_PASSWORD = "new-password";
    private static final String INVALID_OLD_PASSWORD = "invalid-old-password";

    private static final String CURRENT_PASSWORD_HASH = BcryptUtil.bcryptHash(OLD_PASSWORD);

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UpdateUserProfileService updateUserProfileService;

    @Test
    void updateUserProfile_shouldUpdateProfileWithoutPasswordChanging() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.updateProfile(any(UpdateProfileEntityRequest.class)))
                .thenReturn(Optional.of(updateProfileEntityResponse()));

        LocalDateTime beforeUpdate = LocalDateTime.now();

        UpdateProfileResponse response = updateUserProfileService.updateUserProfile(request);

        LocalDateTime afterUpdate = LocalDateTime.now();

        assertEquals(USER_ID, response.id());
        assertEquals(NORMALIZED_EMAIL, response.email());
        assertEquals(TRIMMED_USERNAME, response.username());
        assertEquals(USER, response.role());
        assertEquals(ACTIVE, response.status());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());

        ArgumentCaptor<ExistsUserByEmailAndIdNotEntityRequest> emailCaptor =
                ArgumentCaptor.forClass(ExistsUserByEmailAndIdNotEntityRequest.class);

        verify(userRepository).existsByEmailAndIdNot(emailCaptor.capture());

        ExistsUserByEmailAndIdNotEntityRequest emailRequest = emailCaptor.getValue();

        assertEquals(NORMALIZED_EMAIL, emailRequest.email());
        assertEquals(USER_ID, emailRequest.excludedUserId());

        ArgumentCaptor<ExistsUserByUsernameAndIdNotEntityRequest> usernameCaptor =
                ArgumentCaptor.forClass(ExistsUserByUsernameAndIdNotEntityRequest.class);

        verify(userRepository).existsByUsernameAndIdNot(usernameCaptor.capture());

        ExistsUserByUsernameAndIdNotEntityRequest usernameRequest = usernameCaptor.getValue();

        assertEquals(TRIMMED_USERNAME, usernameRequest.username());
        assertEquals(USER_ID, usernameRequest.excludedUserId());

        ArgumentCaptor<UpdateProfileEntityRequest> updateCaptor =
                ArgumentCaptor.forClass(UpdateProfileEntityRequest.class);

        verify(userRepository).updateProfile(updateCaptor.capture());

        UpdateProfileEntityRequest updateRequest = updateCaptor.getValue();

        assertEquals(USER_ID, updateRequest.userId());
        assertEquals(NORMALIZED_EMAIL, updateRequest.email());
        assertEquals(TRIMMED_USERNAME, updateRequest.username());
        assertEquals(CURRENT_PASSWORD_HASH, updateRequest.passwordHash());
        assertFalse(updateRequest.updatedAt().isBefore(beforeUpdate));
        assertFalse(updateRequest.updatedAt().isAfter(afterUpdate));
    }

    @Test
    void updateUserProfile_shouldUpdateProfileWithPasswordChanging() {
        UpdateProfileRequest request = updateProfileRequestWithPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.updateProfile(any(UpdateProfileEntityRequest.class)))
                .thenReturn(Optional.of(updateProfileEntityResponse()));

        updateUserProfileService.updateUserProfile(request);

        ArgumentCaptor<UpdateProfileEntityRequest> updateCaptor =
                ArgumentCaptor.forClass(UpdateProfileEntityRequest.class);

        verify(userRepository).updateProfile(updateCaptor.capture());

        UpdateProfileEntityRequest updateRequest = updateCaptor.getValue();

        assertNotEquals(CURRENT_PASSWORD_HASH, updateRequest.passwordHash());
        assertTrue(BcryptUtil.matches(NEW_PASSWORD, updateRequest.passwordHash()));
    }

    @Test
    void updateUserProfile_shouldThrowProfileNotFoundException_whenCurrentProfileDoesNotExist() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ProfileNotFoundException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository).findProfileForUpdateById(USER_ID);
        verify(userRepository, never()).existsByEmailAndIdNot(any());
        verify(userRepository, never()).existsByUsernameAndIdNot(any());
        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowProfileIsNotActiveException_whenCurrentProfileIsNotActive() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(blockedProfileForUpdateEntity()));

        assertThrows(
                ProfileIsNotActiveException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository).findProfileForUpdateById(USER_ID);
        verify(userRepository, never()).existsByEmailAndIdNot(any());
        verify(userRepository, never()).existsByUsernameAndIdNot(any());
        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowEmailIsAlreadyUsedException_whenEmailAlreadyUsed() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(true);

        assertThrows(
                EmailIsAlreadyUsedException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository).existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class));
        verify(userRepository, never()).existsByUsernameAndIdNot(any());
        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowUsernameIsAlreadyUsedException_whenUsernameAlreadyUsed() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(true);

        assertThrows(
                UsernameIsAlreadyUsedException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository).existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class));
        verify(userRepository).existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class));
        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowPasswordChangeDataIsIncompleteException_whenOnlyOldPasswordProvided() {
        UpdateProfileRequest request = updateProfileRequestOnlyOldPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        assertThrows(
                PasswordChangeDataIsIncompleteException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowPasswordChangeDataIsIncompleteException_whenOnlyNewPasswordProvided() {
        UpdateProfileRequest request = updateProfileRequestOnlyNewPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        assertThrows(
                PasswordChangeDataIsIncompleteException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowInvalidOldPasswordException_whenOldPasswordIsInvalid() {
        UpdateProfileRequest request = updateProfileRequestWithInvalidOldPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        assertThrows(
                InvalidOldPasswordException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository, never()).updateProfile(any());
    }

    @Test
    void updateUserProfile_shouldThrowProfileNotFoundException_whenProfileDisappearedBeforeUpdate() {
        UpdateProfileRequest request = updateProfileRequestWithoutPassword();

        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userRepository.findProfileForUpdateById(USER_ID))
                .thenReturn(Optional.of(activeProfileForUpdateEntity()));

        when(userRepository.existsByEmailAndIdNot(any(ExistsUserByEmailAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.existsByUsernameAndIdNot(any(ExistsUserByUsernameAndIdNotEntityRequest.class)))
                .thenReturn(false);

        when(userRepository.updateProfile(any(UpdateProfileEntityRequest.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                ProfileNotFoundException.class,
                () -> updateUserProfileService.updateUserProfile(request)
        );

        verify(userRepository).updateProfile(any(UpdateProfileEntityRequest.class));
    }

    private static UpdateProfileRequest updateProfileRequestWithoutPassword() {
        return UpdateProfileRequest.builder()
                .email(RAW_EMAIL)
                .username(RAW_USERNAME)
                .oldPassword(null)
                .newPassword(null)
                .build();
    }

    private static UpdateProfileRequest updateProfileRequestWithPassword() {
        return UpdateProfileRequest.builder()
                .email(RAW_EMAIL)
                .username(RAW_USERNAME)
                .oldPassword(OLD_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();
    }

    private static UpdateProfileRequest updateProfileRequestOnlyOldPassword() {
        return UpdateProfileRequest.builder()
                .email(RAW_EMAIL)
                .username(RAW_USERNAME)
                .oldPassword(OLD_PASSWORD)
                .newPassword(null)
                .build();
    }

    private static UpdateProfileRequest updateProfileRequestOnlyNewPassword() {
        return UpdateProfileRequest.builder()
                .email(RAW_EMAIL)
                .username(RAW_USERNAME)
                .oldPassword(null)
                .newPassword(NEW_PASSWORD)
                .build();
    }

    private static UpdateProfileRequest updateProfileRequestWithInvalidOldPassword() {
        return UpdateProfileRequest.builder()
                .email(RAW_EMAIL)
                .username(RAW_USERNAME)
                .oldPassword(INVALID_OLD_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();
    }

    private static FindProfileForUpdateByIdEntityResponse activeProfileForUpdateEntity() {
        return FindProfileForUpdateByIdEntityResponse.builder()
                .id(USER_ID)
                .email(NORMALIZED_EMAIL)
                .username(TRIMMED_USERNAME)
                .passwordHash(CURRENT_PASSWORD_HASH)
                .role(USER)
                .status(ACTIVE)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static FindProfileForUpdateByIdEntityResponse blockedProfileForUpdateEntity() {
        return FindProfileForUpdateByIdEntityResponse.builder()
                .id(USER_ID)
                .email(NORMALIZED_EMAIL)
                .username(TRIMMED_USERNAME)
                .passwordHash(CURRENT_PASSWORD_HASH)
                .role(USER)
                .status(BLOCKED)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static UpdateProfileEntityResponse updateProfileEntityResponse() {
        return UpdateProfileEntityResponse.builder()
                .id(USER_ID)
                .email(NORMALIZED_EMAIL)
                .username(TRIMMED_USERNAME)
                .role(USER)
                .status(ACTIVE)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
