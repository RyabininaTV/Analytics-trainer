package com.example.tasks.service;

import com.example.repositories.TaskErrorItemsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TasksRepository;
import com.example.tasks.dto.responses.GetRandomTaskResponse;
import com.example.tasks.entity.responses.FindTaskDetailsByIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskErrorItemsByTaskIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskOptionsByTaskIdResponseEntity;
import com.example.tasks.exceptions.TaskRandomNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetRandomTaskService {

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String ERROR_FIND_TASK_TYPE = "ERROR_FIND";

    TasksRepository tasksRepository;
    TaskOptionsRepository taskOptionsRepository;
    TaskErrorItemsRepository taskErrorItemsRepository;

    public GetRandomTaskResponse getRandomTask() {
        FindTaskDetailsByIdResponseEntity task = tasksRepository.findRandom()
                .orElseThrow(TaskRandomNotFoundException::new);

        return GetRandomTaskResponse.builder()
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
                .options(getOptionsIfNeeded(task))
                .errorItems(getErrorItemsIfNeeded(task))
                .build();
    }

    private List<GetRandomTaskResponse.GetRandomTaskOptionResponse> getOptionsIfNeeded(
            @Nonnull FindTaskDetailsByIdResponseEntity task
    ) {
        if (!TEST_TASK_TYPE.equals(task.taskType())) {
            return null;
        }

        return taskOptionsRepository.findByTaskId(task.id()).stream()
                .map(GetRandomTaskService::toOptionResponse)
                .toList();
    }

    private List<GetRandomTaskResponse.GetRandomTaskErrorItemResponse> getErrorItemsIfNeeded(
            @Nonnull FindTaskDetailsByIdResponseEntity task
    ) {
        if (!ERROR_FIND_TASK_TYPE.equals(task.taskType())) {
            return null;
        }

        return taskErrorItemsRepository.findByTaskId(task.id()).stream()
                .map(GetRandomTaskService::toErrorItemResponse)
                .toList();
    }

    private static GetRandomTaskResponse.GetRandomTaskOptionResponse toOptionResponse(
            @Nonnull FindTaskOptionsByTaskIdResponseEntity entity
    ) {
        return GetRandomTaskResponse.GetRandomTaskOptionResponse.builder()
                .id(entity.id())
                .optionText(entity.optionText())
                .build();
    }

    private static GetRandomTaskResponse.GetRandomTaskErrorItemResponse toErrorItemResponse(
            @Nonnull FindTaskErrorItemsByTaskIdResponseEntity entity
    ) {
        return GetRandomTaskResponse.GetRandomTaskErrorItemResponse.builder()
                .id(entity.id())
                .fragmentText(entity.fragmentText())
                .build();
    }

}
