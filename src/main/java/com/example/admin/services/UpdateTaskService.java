package com.example.admin.services;

import com.example.admin.dto.requests.UpdateTaskRequest;
import com.example.admin.dto.responses.UpdateTaskResponse;
import com.example.admin.entities.requests.CreateTaskErrorItemRequestEntity;
import com.example.admin.entities.requests.CreateTaskOptionRequestEntity;
import com.example.admin.entities.requests.UpdateTaskRequestEntity;
import com.example.admin.entities.response.CreateTaskErrorItemResponseEntity;
import com.example.admin.entities.response.CreateTaskOptionResponseEntity;
import com.example.admin.entities.response.UpdateTaskResponseEntity;
import com.example.admin.exceptions.InvalidTaskPayloadException;
import com.example.attempts.exceptions.TaskNotFoundException;
import com.example.repositories.TaskErrorItemsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TasksRepository;
import com.example.repositories.TrainersRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UpdateTaskService {

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String ERROR_FIND_TASK_TYPE = "ERROR_FIND";
    private static final String OPEN_TASK_TYPE = "OPEN";

    TrainersRepository trainersRepository;
    TasksRepository tasksRepository;
    TaskOptionsRepository taskOptionsRepository;
    TaskErrorItemsRepository taskErrorItemsRepository;

    @Transactional
    public UpdateTaskResponse updateTask(Long taskId, @Nonnull UpdateTaskRequest request) {
        if (!trainersRepository.existsActiveById(request.trainerId())) {
            throw new TrainerNotFoundException(taskId);
        }

        validateTaskPayload(request);

        UpdateTaskResponseEntity task = tasksRepository.update(toUpdateTaskRequestEntity(taskId, request))
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        taskOptionsRepository.deleteByTaskId(task.id());
        taskErrorItemsRepository.deleteByTaskId(task.id());

        List<CreateTaskOptionResponseEntity> options = createOptionsIfNeeded(task.id(), request);
        List<CreateTaskErrorItemResponseEntity> errorItems = createErrorItemsIfNeeded(task.id(), request);

        return UpdateTaskResponse.builder()
                .id(task.id())
                .trainerId(task.trainerId())
                .taskType(task.taskType())
                .title(task.title())
                .description(task.description())
                .content(task.content())
                .maxScore(task.maxScore())
                .isActive(task.isActive())
                .autoCheckEnabled(task.autoCheckEnabled())
                .createdAt(task.createdAt())
                .updatedAt(task.updatedAt())
                .options(toOptionResponses(options))
                .errorItems(toErrorItemResponses(errorItems))
                .build();
    }

    private static void validateTaskPayload(@Nonnull UpdateTaskRequest request) {
        if (TEST_TASK_TYPE.equals(request.taskType())) {
            validateTestTaskPayload(request);
            return;
        }

        if (ERROR_FIND_TASK_TYPE.equals(request.taskType())) {
            validateErrorFindTaskPayload(request);
            return;
        }

        if (OPEN_TASK_TYPE.equals(request.taskType())) {
            validateOpenTaskPayload(request);
        }
    }

    private static void validateTestTaskPayload(@Nonnull UpdateTaskRequest request) {
        if (isEmpty(request.options())) {
            throw new InvalidTaskPayloadException("Тестовое задание должно содержать варианты ответа");
        }

        if (!isEmpty(request.errorItems())) {
            throw new InvalidTaskPayloadException("Тестовое задание не должно содержать элементы поиска ошибок");
        }

        boolean hasCorrectOption = request.options().stream()
                .anyMatch(UpdateTaskRequest.UpdateTaskOptionRequest::isCorrect);

        if (!hasCorrectOption) {
            throw new InvalidTaskPayloadException("Тестовое задание должно содержать хотя бы один правильный вариант ответа");
        }
    }

    private static void validateErrorFindTaskPayload(@Nonnull UpdateTaskRequest request) {
        if (isEmpty(request.errorItems())) {
            throw new InvalidTaskPayloadException("Задание на поиск ошибок должно содержать элементы ошибок");
        }

        if (!isEmpty(request.options())) {
            throw new InvalidTaskPayloadException("Задание на поиск ошибок не должно содержать варианты ответа");
        }

        boolean hasErrorItem = request.errorItems().stream()
                .anyMatch(UpdateTaskRequest.UpdateTaskErrorItemRequest::isError);

        if (!hasErrorItem) {
            throw new InvalidTaskPayloadException("Задание на поиск ошибок должно содержать хотя бы один ошибочный элемент");
        }
    }

    private static void validateOpenTaskPayload(@Nonnull UpdateTaskRequest request) {
        if (!isEmpty(request.options())) {
            throw new InvalidTaskPayloadException("Открытое задание не должно содержать варианты ответа");
        }

        if (!isEmpty(request.errorItems())) {
            throw new InvalidTaskPayloadException("Открытое задание не должно содержать элементы поиска ошибок");
        }

        if (request.autoCheckEnabled()) {
            throw new InvalidTaskPayloadException("Открытое задание не должно иметь автоматическую проверку");
        }
    }

    private static boolean isEmpty(List<?> items) {
        return items == null || items.isEmpty();
    }

    private static UpdateTaskRequestEntity toUpdateTaskRequestEntity(
            Long taskId,
            @Nonnull UpdateTaskRequest request
    ) {
        return UpdateTaskRequestEntity.builder()
                .id(taskId)
                .trainerId(request.trainerId())
                .taskType(request.taskType())
                .title(request.title())
                .description(request.description())
                .content(request.content())
                .maxScore(request.maxScore())
                .isActive(request.isActive())
                .autoCheckEnabled(request.autoCheckEnabled())
                .build();
    }

    private List<CreateTaskOptionResponseEntity> createOptionsIfNeeded(
            Long taskId,
            @Nonnull UpdateTaskRequest request
    ) {
        if (!TEST_TASK_TYPE.equals(request.taskType())) {
            return List.of();
        }

        List<CreateTaskOptionRequestEntity> optionRequests = request.options().stream()
                .map(option -> CreateTaskOptionRequestEntity.builder()
                        .taskId(taskId)
                        .optionText(option.optionText())
                        .isCorrect(option.isCorrect())
                        .build()
                )
                .toList();

        return taskOptionsRepository.create(optionRequests);
    }

    private List<CreateTaskErrorItemResponseEntity> createErrorItemsIfNeeded(
            Long taskId,
            @Nonnull UpdateTaskRequest request
    ) {
        if (!ERROR_FIND_TASK_TYPE.equals(request.taskType())) {
            return List.of();
        }

        List<CreateTaskErrorItemRequestEntity> errorItemRequests = request.errorItems().stream()
                .map(errorItem -> CreateTaskErrorItemRequestEntity.builder()
                        .taskId(taskId)
                        .fragmentText(errorItem.fragmentText())
                        .isError(errorItem.isError())
                        .explanation(errorItem.explanation())
                        .build()
                )
                .toList();

        return taskErrorItemsRepository.create(errorItemRequests);
    }

    private static List<UpdateTaskResponse.UpdateTaskOptionResponse> toOptionResponses(
            @Nonnull List<CreateTaskOptionResponseEntity> options
    ) {
        if (options.isEmpty()) {
            return null;
        }

        return options.stream()
                .map(option -> UpdateTaskResponse.UpdateTaskOptionResponse.builder()
                        .id(option.id())
                        .optionText(option.optionText())
                        .isCorrect(option.isCorrect())
                        .build()
                )
                .toList();
    }

    private static List<UpdateTaskResponse.UpdateTaskErrorItemResponse> toErrorItemResponses(
            @Nonnull List<CreateTaskErrorItemResponseEntity> errorItems
    ) {
        if (errorItems.isEmpty()) {
            return null;
        }

        return errorItems.stream()
                .map(errorItem -> UpdateTaskResponse.UpdateTaskErrorItemResponse.builder()
                        .id(errorItem.id())
                        .fragmentText(errorItem.fragmentText())
                        .isError(errorItem.isError())
                        .explanation(errorItem.explanation())
                        .build()
                )
                .toList();
    }

}
