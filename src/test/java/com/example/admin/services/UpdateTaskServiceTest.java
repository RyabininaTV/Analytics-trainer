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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTaskServiceTest {

    private static final Long TASK_ID = 100L;
    private static final Long TRAINER_ID = 10L;

    private static final Long FIRST_OPTION_ID = 1L;
    private static final Long SECOND_OPTION_ID = 2L;

    private static final Long FIRST_ERROR_ITEM_ID = 1L;
    private static final Long SECOND_ERROR_ITEM_ID = 2L;

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String ERROR_FIND_TASK_TYPE = "ERROR_FIND";
    private static final String OPEN_TASK_TYPE = "OPEN";

    private static final String TITLE = "Заголовок";
    private static final String DESCRIPTION = "Описание";
    private static final String CONTENT = "Контент";

    private static final Integer MAX_SCORE = 10;

    private static final String FIRST_OPTION_TEXT = "Ответ 1";
    private static final String SECOND_OPTION_TEXT = "Ответ 2";

    private static final String ERROR_FRAGMENT_TEXT = "ошибка";
    private static final String NORMAL_FRAGMENT_TEXT = "норма";
    private static final String EXPLANATION = "Пояснение";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    TrainersRepository trainersRepository;

    @Mock
    TasksRepository tasksRepository;

    @Mock
    TaskOptionsRepository taskOptionsRepository;

    @Mock
    TaskErrorItemsRepository taskErrorItemsRepository;

    @Captor
    ArgumentCaptor<List<CreateTaskOptionRequestEntity>> optionsCaptor;

    @Captor
    ArgumentCaptor<List<CreateTaskErrorItemRequestEntity>> errorItemsCaptor;

    @InjectMocks
    UpdateTaskService updateTaskService;

    @Test
    void updateTask_shouldUpdateTestTaskWithOptions() {
        UpdateTaskRequest request = testTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.update(any(UpdateTaskRequestEntity.class)))
                .thenReturn(Optional.of(taskEntity(TEST_TASK_TYPE, true)));

        when(taskOptionsRepository.create(anyList()))
                .thenReturn(List.of(
                        optionEntity(FIRST_OPTION_ID, FIRST_OPTION_TEXT, true),
                        optionEntity(SECOND_OPTION_ID, SECOND_OPTION_TEXT, false)
                ));

        UpdateTaskResponse response = updateTaskService.updateTask(TASK_ID, request);

        assertEquals(TASK_ID, response.id());
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals(TEST_TASK_TYPE, response.taskType());
        assertEquals(TITLE, response.title());
        assertEquals(DESCRIPTION, response.description());
        assertEquals(CONTENT, response.content());
        assertEquals(MAX_SCORE, response.maxScore());
        assertTrue(response.isActive());
        assertTrue(response.autoCheckEnabled());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());

        assertNotNull(response.options());
        assertEquals(2, response.options().size());
        assertNull(response.errorItems());

        assertEquals(FIRST_OPTION_ID, response.options().getFirst().id());
        assertEquals(FIRST_OPTION_TEXT, response.options().getFirst().optionText());
        assertTrue(response.options().getFirst().isCorrect());

        ArgumentCaptor<UpdateTaskRequestEntity> updateTaskCaptor =
                ArgumentCaptor.forClass(UpdateTaskRequestEntity.class);

        verify(tasksRepository).update(updateTaskCaptor.capture());

        UpdateTaskRequestEntity updateTaskRequest = updateTaskCaptor.getValue();

        assertEquals(TASK_ID, updateTaskRequest.id());
        assertEquals(TRAINER_ID, updateTaskRequest.trainerId());
        assertEquals(TEST_TASK_TYPE, updateTaskRequest.taskType());
        assertEquals(TITLE, updateTaskRequest.title());
        assertEquals(DESCRIPTION, updateTaskRequest.description());
        assertEquals(CONTENT, updateTaskRequest.content());
        assertEquals(MAX_SCORE, updateTaskRequest.maxScore());
        assertTrue(updateTaskRequest.isActive());
        assertTrue(updateTaskRequest.autoCheckEnabled());

        verify(taskOptionsRepository).create(optionsCaptor.capture());

        List<CreateTaskOptionRequestEntity> optionRequests = optionsCaptor.getValue();

        assertEquals(2, optionRequests.size());
        assertEquals(TASK_ID, optionRequests.getFirst().taskId());
        assertEquals(FIRST_OPTION_TEXT, optionRequests.getFirst().optionText());
        assertTrue(optionRequests.getFirst().isCorrect());

        verify(taskOptionsRepository).deleteByTaskId(TASK_ID);
        verify(taskErrorItemsRepository).deleteByTaskId(TASK_ID);
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void updateTask_shouldUpdateErrorFindTaskWithErrorItems() {
        UpdateTaskRequest request = errorFindTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.update(any(UpdateTaskRequestEntity.class)))
                .thenReturn(Optional.of(taskEntity(ERROR_FIND_TASK_TYPE, true)));

        when(taskErrorItemsRepository.create(anyList()))
                .thenReturn(List.of(
                        errorItemEntity(FIRST_ERROR_ITEM_ID, ERROR_FRAGMENT_TEXT, true, EXPLANATION),
                        errorItemEntity(SECOND_ERROR_ITEM_ID, NORMAL_FRAGMENT_TEXT, false, null)
                ));

        UpdateTaskResponse response = updateTaskService.updateTask(TASK_ID, request);

        assertEquals(TASK_ID, response.id());
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals(ERROR_FIND_TASK_TYPE, response.taskType());

        assertNull(response.options());
        assertNotNull(response.errorItems());
        assertEquals(2, response.errorItems().size());

        assertEquals(FIRST_ERROR_ITEM_ID, response.errorItems().getFirst().id());
        assertEquals(ERROR_FRAGMENT_TEXT, response.errorItems().getFirst().fragmentText());
        assertTrue(response.errorItems().getFirst().isError());
        assertEquals(EXPLANATION, response.errorItems().getFirst().explanation());

        verify(taskErrorItemsRepository).create(errorItemsCaptor.capture());

        List<CreateTaskErrorItemRequestEntity> errorItemRequests = errorItemsCaptor.getValue();

        assertEquals(2, errorItemRequests.size());
        assertEquals(TASK_ID, errorItemRequests.getFirst().taskId());
        assertEquals(ERROR_FRAGMENT_TEXT, errorItemRequests.getFirst().fragmentText());
        assertTrue(errorItemRequests.getFirst().isError());
        assertEquals(EXPLANATION, errorItemRequests.getFirst().explanation());

        verify(taskOptionsRepository).deleteByTaskId(TASK_ID);
        verify(taskErrorItemsRepository).deleteByTaskId(TASK_ID);
        verify(taskOptionsRepository, never()).create(anyList());
    }

    @Test
    void updateTask_shouldUpdateOpenTaskWithoutOptionsAndErrorItems() {
        UpdateTaskRequest request = openTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.update(any(UpdateTaskRequestEntity.class)))
                .thenReturn(Optional.of(taskEntity(OPEN_TASK_TYPE, false)));

        UpdateTaskResponse response = updateTaskService.updateTask(TASK_ID, request);

        assertEquals(TASK_ID, response.id());
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals(OPEN_TASK_TYPE, response.taskType());
        assertFalse(response.autoCheckEnabled());

        assertNull(response.options());
        assertNull(response.errorItems());

        verify(tasksRepository).update(any(UpdateTaskRequestEntity.class));
        verify(taskOptionsRepository).deleteByTaskId(TASK_ID);
        verify(taskErrorItemsRepository).deleteByTaskId(TASK_ID);
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void updateTask_shouldThrowTrainerNotFoundException_whenTrainerDoesNotExist() {
        UpdateTaskRequest request = testTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(false);

        assertThrows(
                TrainerNotFoundException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
        verify(taskOptionsRepository, never()).deleteByTaskId(any());
        verify(taskErrorItemsRepository, never()).deleteByTaskId(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void updateTask_shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {
        UpdateTaskRequest request = testTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.update(any(UpdateTaskRequestEntity.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository).update(any(UpdateTaskRequestEntity.class));
        verify(taskOptionsRepository, never()).deleteByTaskId(any());
        verify(taskErrorItemsRepository, never()).deleteByTaskId(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasNoOptions() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of())
                .errorItems(null)
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasNoCorrectOption() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(false)
                                .build()
                ))
                .errorItems(null)
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasErrorItems() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build()
                ))
                .errorItems(List.of(
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(ERROR_FRAGMENT_TEXT)
                                .isError(true)
                                .explanation(EXPLANATION)
                                .build()
                ))
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasNoErrorItems() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(ERROR_FIND_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(null)
                .errorItems(List.of())
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasOptions() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(ERROR_FIND_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build()
                ))
                .errorItems(List.of(
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(ERROR_FRAGMENT_TEXT)
                                .isError(true)
                                .explanation(EXPLANATION)
                                .build()
                ))
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasNoErrorMarkedItem() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(ERROR_FIND_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(null)
                .errorItems(List.of(
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(NORMAL_FRAGMENT_TEXT)
                                .isError(false)
                                .explanation(null)
                                .build()
                ))
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasOptions() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(OPEN_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(false)
                .options(List.of(
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build()
                ))
                .errorItems(null)
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasErrorItems() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(OPEN_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(false)
                .options(null)
                .errorItems(List.of(
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(ERROR_FRAGMENT_TEXT)
                                .isError(true)
                                .explanation(EXPLANATION)
                                .build()
                ))
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    @Test
    void updateTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasAutoCheckEnabled() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(OPEN_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(null)
                .errorItems(null)
                .build();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        assertThrows(
                InvalidTaskPayloadException.class,
                () -> updateTaskService.updateTask(TASK_ID, request)
        );

        verify(tasksRepository, never()).update(any());
    }

    private static UpdateTaskRequest testTaskRequest() {
        return UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build(),
                        UpdateTaskRequest.UpdateTaskOptionRequest.builder()
                                .optionText(SECOND_OPTION_TEXT)
                                .isCorrect(false)
                                .build()
                ))
                .errorItems(null)
                .build();
    }

    private static UpdateTaskRequest errorFindTaskRequest() {
        return UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(ERROR_FIND_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(null)
                .errorItems(List.of(
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(ERROR_FRAGMENT_TEXT)
                                .isError(true)
                                .explanation(EXPLANATION)
                                .build(),
                        UpdateTaskRequest.UpdateTaskErrorItemRequest.builder()
                                .fragmentText(NORMAL_FRAGMENT_TEXT)
                                .isError(false)
                                .explanation(null)
                                .build()
                ))
                .build();
    }

    private static UpdateTaskRequest openTaskRequest() {
        return UpdateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(OPEN_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(false)
                .options(null)
                .errorItems(null)
                .build();
    }

    private static UpdateTaskResponseEntity taskEntity(
            String taskType,
            boolean autoCheckEnabled
    ) {
        return UpdateTaskResponseEntity.builder()
                .id(TASK_ID)
                .trainerId(TRAINER_ID)
                .taskType(taskType)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(autoCheckEnabled)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static CreateTaskOptionResponseEntity optionEntity(
            Long id,
            String optionText,
            boolean isCorrect
    ) {
        return CreateTaskOptionResponseEntity.builder()
                .id(id)
                .optionText(optionText)
                .isCorrect(isCorrect)
                .build();
    }

    private static CreateTaskErrorItemResponseEntity errorItemEntity(
            Long id,
            String fragmentText,
            boolean isError,
            String explanation
    ) {
        return CreateTaskErrorItemResponseEntity.builder()
                .id(id)
                .fragmentText(fragmentText)
                .isError(isError)
                .explanation(explanation)
                .build();
    }

}
