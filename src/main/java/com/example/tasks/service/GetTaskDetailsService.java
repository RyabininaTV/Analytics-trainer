package com.example.tasks.service;

import com.example.attempts.exceptions.TaskNotFoundException;
import com.example.repositories.TaskErrorItemsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TasksRepository;
import com.example.tasks.dto.responses.GetTaskDetailsResponse;
import com.example.tasks.entity.responses.FindTaskDetailsByIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskErrorItemsByTaskIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskOptionsByTaskIdResponseEntity;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetTaskDetailsService {

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String ERROR_FIND_TASK_TYPE = "ERROR_FIND";

    TasksRepository tasksRepository;
    TaskOptionsRepository taskOptionsRepository;
    TaskErrorItemsRepository taskErrorItemsRepository;

    public GetTaskDetailsResponse getTaskDetails(@Nonnull Long id) {
        FindTaskDetailsByIdResponseEntity task = tasksRepository.findDetailsById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        List<GetTaskDetailsResponse.GetTaskOptionResponse> options = getOptionsIfNeeded(task);
        List<GetTaskDetailsResponse.GetTaskErrorItemResponse> errorItems = getErrorItemsIfNeeded(task);

        return GetTaskDetailsResponse.builder()
                .id(task.id())
                .trainerId(task.trainerId())
                .trainerTitle(task.trainerTitle())
                .taskType(task.taskType())
                .title(task.title())
                .description(task.description())
                .content(task.content())
                .maxScore(task.maxScore())
                .autoCheckEnabled(task.autoCheckEnabled())
                .createdAt(task.createdAt())
                .updatedAt(task.updatedAt())
                .options(options)
                .errorItems(errorItems)
                .build();
    }

    private List<GetTaskDetailsResponse.GetTaskOptionResponse> getOptionsIfNeeded(
            @Nonnull FindTaskDetailsByIdResponseEntity task
    ) {
        if (!TEST_TASK_TYPE.equals(task.taskType())) {
            return null;
        }

        return taskOptionsRepository.findByTaskId(task.id()).stream()
                .map(GetTaskDetailsService::toOptionResponse)
                .toList();
    }

    private List<GetTaskDetailsResponse.GetTaskErrorItemResponse> getErrorItemsIfNeeded(
            @Nonnull FindTaskDetailsByIdResponseEntity task
    ) {
        if (!ERROR_FIND_TASK_TYPE.equals(task.taskType())) {
            return null;
        }

        return taskErrorItemsRepository.findByTaskId(task.id()).stream()
                .map(GetTaskDetailsService::toErrorItemResponse)
                .toList();
    }

    private static GetTaskDetailsResponse.GetTaskOptionResponse toOptionResponse(
            @Nonnull FindTaskOptionsByTaskIdResponseEntity entity
    ) {
        return GetTaskDetailsResponse.GetTaskOptionResponse.builder()
                .id(entity.id())
                .optionText(entity.optionText())
                .build();
    }

    private static GetTaskDetailsResponse.GetTaskErrorItemResponse toErrorItemResponse(
            @Nonnull FindTaskErrorItemsByTaskIdResponseEntity entity
    ) {
        return GetTaskDetailsResponse.GetTaskErrorItemResponse.builder()
                .id(entity.id())
                .fragmentText(entity.fragmentText())
                .build();
    }

}
