package com.example.trainers.services;

import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.trainers.dto.responses.UserProgressByTrainerIdResponse;
import com.example.trainers.entities.requests.GetUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.responses.UserProgressByTrainerIdEntityResponse;
import com.example.trainers.exceptions.UserProgressNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUserProgressByTrainerIdService {

    CurrentUserContext currentUserContext;

    UserProgressRepository userProgressRepository;

    public UserProgressByTrainerIdResponse getUserProgressByTrainerId(long trainerId) {
        GetUserProgressByTrainerIdEntityRequest request = GetUserProgressByTrainerIdEntityRequest.builder()
                .userId(currentUserContext.require().id())
                .trainerId(trainerId)
                .build();

        UserProgressByTrainerIdEntityResponse response = userProgressRepository.getUserProgressByTrainerId(request)
                .orElseThrow(UserProgressNotFoundException::new);

        return mapToDto(response);
    }

    private static UserProgressByTrainerIdResponse mapToDto(@Nonnull UserProgressByTrainerIdEntityResponse entity) {
        return UserProgressByTrainerIdResponse.builder()
                .id(entity.id())
                .userId(entity.userId())
                .trainerId(entity.trainerId())
                .completedTasksCount(entity.completedTasksCount())
                .totalTasksCount(entity.totalTasksCount())
                .totalScore(entity.totalScore())
                .completionPercent(entity.completionPercent())
                .lastActivityAt(entity.lastActivityAt())
                .build();
    }

}
