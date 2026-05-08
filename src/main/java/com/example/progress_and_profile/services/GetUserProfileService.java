package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProfileResponse;
import com.example.progress_and_profile.entity.responses.GetProfileResponseEntity;
import com.example.progress_and_profile.exceptions.ProfileIsNotActiveException;
import com.example.progress_and_profile.exceptions.ProfileNotFoundException;
import com.example.repositories.UserRepository;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUserProfileService {

    CurrentUserContext currentUserContext;

    UserRepository userRepository;

    public ProfileResponse getUserProfile() {
        Long userId = currentUserContext.require().id();

        GetProfileResponseEntity entity = userRepository.getProfileById(userId)
                .orElseThrow(ProfileNotFoundException::new);

        if (entity.status() != ACTIVE) {
            throw new ProfileIsNotActiveException();
        }

        return mapToDto(entity);
    }

    private static ProfileResponse mapToDto(@Nonnull GetProfileResponseEntity entity) {
        return ProfileResponse.builder()
                .id(entity.id())
                .email(entity.email())
                .username(entity.username())
                .role(entity.role())
                .status(entity.status())
                .createdAt(entity.createdAt())
                .updatedAt(entity.updatedAt())
                .build();
    }

}
