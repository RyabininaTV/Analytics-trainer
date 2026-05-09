package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProgressItemResponse;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUserProgressByAllTrainersService {

    CurrentUserContext currentUserContext;

    UserProgressRepository userProgressRepository;

    public List<ProgressItemResponse> getUserProgressByAllTrainers() {
        return userProgressRepository.findByUserId(currentUserContext.require().id())
                .stream()
                .map(entity -> ProgressItemResponse.builder()
                        .trainerId(entity.trainerId())
                        .completedTasksCount(entity.completedTasksCount())
                        .totalTasksCount(entity.totalTasksCount())
                        .totalScore(entity.totalScore())
                        .completionPercent(entity.completionPercent())
                        .lastActivityAt(entity.lastActivityAt())
                        .build()
                )
                .toList();
    }

}
