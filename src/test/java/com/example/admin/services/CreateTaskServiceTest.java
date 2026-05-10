package com.example.admin.services;

import com.example.admin.dto.requests.CreateTaskRequest;
import com.example.admin.dto.responses.CreateTaskResponse;
import com.example.admin.entities.requests.CreateTaskRequestEntity;
import com.example.admin.entities.response.CreateTaskErrorItemResponseEntity;
import com.example.admin.entities.response.CreateTaskOptionResponseEntity;
import com.example.admin.entities.response.CreateTaskResponseEntity;
import com.example.admin.exceptions.InvalidTaskPayloadException;
import com.example.repositories.TaskErrorItemsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TasksRepository;
import com.example.repositories.TrainersRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceTest {

    private static final Long TRAINER_ID = 10L;
    private static final Long TASK_ID = 100L;

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

    @InjectMocks
    CreateTaskService createTaskService;

    @Test
    void createTask_shouldCreateTestTaskWithOptions() {
        CreateTaskRequest request = testTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.create(any(CreateTaskRequestEntity.class)))
                .thenReturn(taskEntity(TEST_TASK_TYPE, true));

        when(taskOptionsRepository.create(anyList()))
                .thenReturn(List.of(
                        optionEntity(FIRST_OPTION_ID, FIRST_OPTION_TEXT, true),
                        optionEntity(SECOND_OPTION_ID, SECOND_OPTION_TEXT, false)
                ));

        CreateTaskResponse response = createTaskService.createTask(request);

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

        ArgumentCaptor<CreateTaskRequestEntity> taskCaptor =
                ArgumentCaptor.forClass(CreateTaskRequestEntity.class);

        verify(tasksRepository).create(taskCaptor.capture());

        CreateTaskRequestEntity taskRequest = taskCaptor.getValue();

        assertEquals(TRAINER_ID, taskRequest.trainerId());
        assertEquals(TEST_TASK_TYPE, taskRequest.taskType());
        assertEquals(TITLE, taskRequest.title());
        assertEquals(DESCRIPTION, taskRequest.description());
        assertEquals(CONTENT, taskRequest.content());
        assertEquals(MAX_SCORE, taskRequest.maxScore());
        assertTrue(taskRequest.isActive());
        assertTrue(taskRequest.autoCheckEnabled());

        verify(taskOptionsRepository).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldCreateErrorFindTaskWithErrorItems() {
        CreateTaskRequest request = errorFindTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.create(any(CreateTaskRequestEntity.class)))
                .thenReturn(taskEntity(ERROR_FIND_TASK_TYPE, true));

        when(taskErrorItemsRepository.create(anyList()))
                .thenReturn(List.of(
                        errorItemEntity(FIRST_ERROR_ITEM_ID, ERROR_FRAGMENT_TEXT, true, EXPLANATION),
                        errorItemEntity(SECOND_ERROR_ITEM_ID, NORMAL_FRAGMENT_TEXT, false, null)
                ));

        CreateTaskResponse response = createTaskService.createTask(request);

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

        verify(taskErrorItemsRepository).create(anyList());
        verify(taskOptionsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldCreateOpenTaskWithoutOptionsAndErrorItems() {
        CreateTaskRequest request = openTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(tasksRepository.create(any(CreateTaskRequestEntity.class)))
                .thenReturn(taskEntity(OPEN_TASK_TYPE, false));

        CreateTaskResponse response = createTaskService.createTask(request);

        assertEquals(TASK_ID, response.id());
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals(OPEN_TASK_TYPE, response.taskType());
        assertFalse(response.autoCheckEnabled());

        assertNull(response.options());
        assertNull(response.errorItems());

        verify(tasksRepository).create(any(CreateTaskRequestEntity.class));
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldThrowTrainerNotFoundException_whenTrainerDoesNotExist() {
        CreateTaskRequest request = testTaskRequest();

        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(false);

        assertThrows(
                TrainerNotFoundException.class,
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasNoOptions() {
        CreateTaskRequest request = CreateTaskRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasNoCorrectOption() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenTestTaskHasErrorItems() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build()
                ))
                .errorItems(List.of(
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasNoErrorItems() {
        CreateTaskRequest request = CreateTaskRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
        verify(taskOptionsRepository, never()).create(anyList());
        verify(taskErrorItemsRepository, never()).create(anyList());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasOptions() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(ERROR_FIND_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build()
                ))
                .errorItems(List.of(
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenErrorFindTaskHasNoErrorMarkedItem() {
        CreateTaskRequest request = CreateTaskRequest.builder()
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
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasOptions() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(OPEN_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(false)
                .options(List.of(
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasErrorItems() {
        CreateTaskRequest request = CreateTaskRequest.builder()
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
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    @Test
    void createTask_shouldThrowInvalidTaskPayloadException_whenOpenTaskHasAutoCheckEnabled() {
        CreateTaskRequest request = CreateTaskRequest.builder()
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
                () -> createTaskService.createTask(request)
        );

        verify(tasksRepository, never()).create(any());
    }

    private static CreateTaskRequest testTaskRequest() {
        return CreateTaskRequest.builder()
                .trainerId(TRAINER_ID)
                .taskType(TEST_TASK_TYPE)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .options(List.of(
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
                                .optionText(FIRST_OPTION_TEXT)
                                .isCorrect(true)
                                .build(),
                        CreateTaskRequest.CreateTaskOptionRequest.builder()
                                .optionText(SECOND_OPTION_TEXT)
                                .isCorrect(false)
                                .build()
                ))
                .errorItems(null)
                .build();
    }

    private static CreateTaskRequest errorFindTaskRequest() {
        return CreateTaskRequest.builder()
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
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
                                .fragmentText(ERROR_FRAGMENT_TEXT)
                                .isError(true)
                                .explanation(EXPLANATION)
                                .build(),
                        CreateTaskRequest.CreateTaskErrorItemRequest.builder()
                                .fragmentText(NORMAL_FRAGMENT_TEXT)
                                .isError(false)
                                .explanation(null)
                                .build()
                ))
                .build();
    }

    private static CreateTaskRequest openTaskRequest() {
        return CreateTaskRequest.builder()
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

    private static CreateTaskResponseEntity taskEntity(
            String taskType,
            boolean autoCheckEnabled
    ) {
        return CreateTaskResponseEntity.builder()
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
