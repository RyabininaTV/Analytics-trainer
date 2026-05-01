package com.example.trainers.services;

import com.example.repositories.TasksRepository;
import com.example.trainers.dto.responses.TaskByTrainerIdResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetTrainerTasksByIdService {

    TasksRepository tasksRepository;

    public List<TaskByTrainerIdResponse> getTrainerTasksById(long id) {
        return tasksRepository.getTasksByTrainerId(id)
                .stream()
                .map(entity -> TaskByTrainerIdResponse.builder()
                        .id(entity.id())
                        .trainerId(entity.trainerId())
                        .taskType(entity.taskType())
                        .title(entity.title())
                        .description(entity.description())
                        .content(entity.content())
                        .maxScore(entity.maxScore())
                        .isActive(entity.isActive())
                        .autoCheckEnabled(entity.autoCheckEnabled())
                        .createdAt(entity.createdAt())
                        .updatedAt(entity.updatedAt())
                        .build()
                )
                .toList();
    }

}
