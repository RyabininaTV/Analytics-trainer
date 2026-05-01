package com.example.trainers.services;

import com.example.repositories.AttemptsRepository;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import com.example.trainers.entities.requests.DeleteUserProgressByTrainerIdEntityRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ResetUserProgressByTrainerIdService {

    CurrentUserContext currentUserContext;

    UserProgressRepository userProgressRepository;
    AttemptsRepository attemptsRepository;

    @Transactional
    public void resetUserProgressByTrainerId(long trainerId) {
        Long userId = currentUserContext.require().id();

        userProgressRepository.deleteUserProgressByTrainerId(DeleteUserProgressByTrainerIdEntityRequest.builder()
                .userId(userId)
                .trainerId(trainerId)
                .build()
        );

        attemptsRepository.deleteUserAttemptsByTrainerId(DeleteUserAttemptsByTrainerIdEntityRequest.builder()
                .userId(userId)
                .trainerId(trainerId)
                .build()
        );
    }

}
