package com.example.tasks.service;

import com.example.repositories.TasksRepository;
import com.example.tasks.dto.responses.GetTaskItemResponse;
import com.example.tasks.entity.requests.FindTasksRequestEntity;
import com.example.tasks.entity.responses.FindTasksResponseEntity;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetTasksService {

    TasksRepository tasksRepository;

    public List<GetTaskItemResponse> getTasks(Long trainerId) {
        FindTasksRequestEntity request = FindTasksRequestEntity.builder()
                .trainerId(trainerId)
                .build();

        List<FindTasksResponseEntity> tasks = tasksRepository.findTasks(request);

        return tasks.stream()
                .map(GetTasksService::toResponse)
                .toList();
    }

    private static GetTaskItemResponse toResponse(@Nonnull FindTasksResponseEntity entity) {
        return GetTaskItemResponse.builder()
                .id(entity.id())
                .trainerId(entity.trainerId())
                .trainerTitle(entity.trainerTitle())
                .taskType(entity.taskType())
                .title(entity.title())
                .description(entity.description())
                .content(entity.content())
                .maxScore(entity.maxScore())
                .autoCheckEnabled(entity.autoCheckEnabled())
                .createdAt(entity.createdAt())
                .updatedAt(entity.updatedAt())
                .build();
    }

}
