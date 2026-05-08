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
import com.example.progress_and_profile.exceptions.*;
import com.example.repositories.UserRepository;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.utils.EmailUtil;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UpdateUserProfileService {

    CurrentUserContext currentUserContext;

    UserRepository userRepository;

    @Transactional
    public UpdateProfileResponse updateUserProfile(@Nonnull UpdateProfileRequest request) {
        Long userId = currentUserContext.require().id();

        FindProfileForUpdateByIdEntityResponse currentProfile = findProfileForUpdateById(userId);

        ValidateEmailAndUsernameData validateData = validateEmailAndUsername(ValidateEmailAndUsernameData.builder()
                .userId(userId)
                .email(request.email())
                .username(request.username())
                .build()
        );

        String passwordHash = resolvePasswordHash(request, currentProfile);

        UpdateProfileEntityResponse updatedProfile = userRepository.updateProfile(UpdateProfileEntityRequest.builder()
                        .userId(userId)
                        .email(validateData.email())
                        .username(validateData.username())
                        .passwordHash(passwordHash)
                        .updatedAt(LocalDateTime.now())
                        .build()
                )
                .orElseThrow(ProfileNotFoundException::new);

        return mapToDto(updatedProfile);
    }

    @Nonnull
    private FindProfileForUpdateByIdEntityResponse findProfileForUpdateById(Long userId) {
        FindProfileForUpdateByIdEntityResponse response = userRepository.findProfileForUpdateById(userId)
                .orElseThrow(ProfileNotFoundException::new);

        if (response.status() != ACTIVE) {
            throw new ProfileIsNotActiveException();
        }

        return response;
    }

    private ValidateEmailAndUsernameData validateEmailAndUsername(@Nonnull ValidateEmailAndUsernameData data) {
        String email = EmailUtil.normalize(data.email());
        if (userRepository.existsByEmailAndIdNot(ExistsUserByEmailAndIdNotEntityRequest.builder()
                .email(email)
                .excludedUserId(data.userId())
                .build()
        )) {
            throw new EmailIsAlreadyUsedException();
        }

        String username = data.username().trim();
        if (userRepository.existsByUsernameAndIdNot(ExistsUserByUsernameAndIdNotEntityRequest.builder()
                .username(username)
                .excludedUserId(data.userId())
                .build()
        )) {
            throw new UsernameIsAlreadyUsedException();
        }

        return ValidateEmailAndUsernameData.builder()
                .userId(data.userId())
                .email(email)
                .username(username)
                .build();
    }

    private static String resolvePasswordHash(
            @Nonnull UpdateProfileRequest request,
            FindProfileForUpdateByIdEntityResponse currentProfile
    ) {
        boolean oldPasswordProvided = request.oldPassword() != null;
        boolean newPasswordProvided = request.newPassword() != null;

        if (!oldPasswordProvided && !newPasswordProvided) {
            return currentProfile.passwordHash();
        }

        if (!oldPasswordProvided || !newPasswordProvided) {
            throw new PasswordChangeDataIsIncompleteException();
        }

        if (!BcryptUtil.matches(request.oldPassword(), currentProfile.passwordHash())) {
            throw new InvalidOldPasswordException();
        }

        return BcryptUtil.bcryptHash(request.newPassword());
    }

    private static UpdateProfileResponse mapToDto(@Nonnull UpdateProfileEntityResponse entity) {
        return UpdateProfileResponse.builder()
                .id(entity.id())
                .email(entity.email())
                .username(entity.username())
                .role(entity.role())
                .status(entity.status())
                .createdAt(entity.createdAt())
                .updatedAt(entity.updatedAt())
                .build();
    }

    @Builder
    private record ValidateEmailAndUsernameData(

            Long userId,
            String email,
            String username

    ) {}

}
