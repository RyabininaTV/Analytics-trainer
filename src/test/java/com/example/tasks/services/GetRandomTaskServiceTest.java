package com.example.tasks.services;

import com.example.repositories.TaskErrorItemsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TasksRepository;
import com.example.tasks.dto.responses.GetRandomTaskResponse;
import com.example.tasks.entity.responses.FindTaskDetailsByIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskErrorItemsByTaskIdResponseEntity;
import com.example.tasks.entity.responses.FindTaskOptionsByTaskIdResponseEntity;
import com.example.tasks.exceptions.TaskRandomNotFoundException;
import com.example.tasks.service.GetRandomTaskService;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRandomTaskServiceTest {

    private static final Long TASK_ID = 100L;
    private static final Long TRAINER_ID = 10L;

    private static final Long FIRST_OPTION_ID = 1L;
    private static final Long SECOND_OPTION_ID = 2L;

    private static final Long FIRST_ERROR_ITEM_ID = 1L;
    private static final Long SECOND_ERROR_ITEM_ID = 2L;

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String ERROR_FIND_TASK_TYPE = "ERROR_FIND";
    private static final String OPEN_TASK_TYPE = "OPEN";

    private static final String TRAINER_TITLE = "SQL тренажёр";
    private static final String TITLE = "Заголовок";
    private static final String DESCRIPTION = "Описание";
    private static final String CONTENT = "Контент";

    private static final String FIRST_OPTION_TEXT = "Ответ 1";
    private static final String SECOND_OPTION_TEXT = "Ответ 2";

    private static final String FIRST_FRAGMENT_TEXT = "Фрагмент 1";
    private static final String SECOND_FRAGMENT_TEXT = "Фрагмент 2";

    private static final Integer MAX_SCORE = 10;

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    TasksRepository tasksRepository;

    @Mock
    TaskOptionsRepository taskOptionsRepository;

    @Mock
    TaskErrorItemsRepository taskErrorItemsRepository;

    @InjectMocks
    GetRandomTaskService getRandomTaskService;

    @Test
    void getRandomTask_shouldReturnTestTaskWithOptions() {
        when(tasksRepository.findRandom())
                .thenReturn(Optional.of(taskEntity(TEST_TASK_TYPE, true)));

        when(taskOptionsRepository.findByTaskId(TASK_ID))
                .thenReturn(List.of(
                        firstOptionEntity(),
                        secondOptionEntity()
                ));

        GetRandomTaskResponse response = getRandomTaskService.getRandomTask();

        assertBaseTaskResponse(response, TEST_TASK_TYPE, true);

        assertNotNull(response.options());
        assertEquals(2, response.options().size());
        assertNull(response.errorItems());

        GetRandomTaskResponse.GetRandomTaskOptionResponse firstOption = response.options().getFirst();

        assertEquals(FIRST_OPTION_ID, firstOption.id());
        assertEquals(FIRST_OPTION_TEXT, firstOption.optionText());

        GetRandomTaskResponse.GetRandomTaskOptionResponse secondOption = response.options().get(1);

        assertEquals(SECOND_OPTION_ID, secondOption.id());
        assertEquals(SECOND_OPTION_TEXT, secondOption.optionText());

        verify(tasksRepository).findRandom();
        verify(taskOptionsRepository).findByTaskId(TASK_ID);
        verify(taskErrorItemsRepository, never()).findByTaskId(TASK_ID);
    }

    @Test
    void getRandomTask_shouldReturnErrorFindTaskWithErrorItems() {
        when(tasksRepository.findRandom())
                .thenReturn(Optional.of(taskEntity(ERROR_FIND_TASK_TYPE, true)));

        when(taskErrorItemsRepository.findByTaskId(TASK_ID))
                .thenReturn(List.of(
                        firstErrorItemEntity(),
                        secondErrorItemEntity()
                ));

        GetRandomTaskResponse response = getRandomTaskService.getRandomTask();

        assertBaseTaskResponse(response, ERROR_FIND_TASK_TYPE, true);

        assertNull(response.options());
        assertNotNull(response.errorItems());
        assertEquals(2, response.errorItems().size());

        GetRandomTaskResponse.GetRandomTaskErrorItemResponse firstErrorItem = response.errorItems().getFirst();

        assertEquals(FIRST_ERROR_ITEM_ID, firstErrorItem.id());
        assertEquals(FIRST_FRAGMENT_TEXT, firstErrorItem.fragmentText());

        GetRandomTaskResponse.GetRandomTaskErrorItemResponse secondErrorItem = response.errorItems().get(1);

        assertEquals(SECOND_ERROR_ITEM_ID, secondErrorItem.id());
        assertEquals(SECOND_FRAGMENT_TEXT, secondErrorItem.fragmentText());

        verify(tasksRepository).findRandom();
        verify(taskOptionsRepository, never()).findByTaskId(TASK_ID);
        verify(taskErrorItemsRepository).findByTaskId(TASK_ID);
    }

    @Test
    void getRandomTask_shouldReturnOpenTaskWithoutOptionsAndErrorItems() {
        when(tasksRepository.findRandom())
                .thenReturn(Optional.of(taskEntity(OPEN_TASK_TYPE, false)));

        GetRandomTaskResponse response = getRandomTaskService.getRandomTask();

        assertBaseTaskResponse(response, OPEN_TASK_TYPE, false);

        assertNull(response.options());
        assertNull(response.errorItems());

        verify(tasksRepository).findRandom();
        verify(taskOptionsRepository, never()).findByTaskId(TASK_ID);
        verify(taskErrorItemsRepository, never()).findByTaskId(TASK_ID);
    }

    @Test
    void getRandomTask_shouldThrowTaskRandomNotFoundException_whenRandomTaskDoesNotExist() {
        when(tasksRepository.findRandom())
                .thenReturn(Optional.empty());

        assertThrows(
                TaskRandomNotFoundException.class,
                () -> getRandomTaskService.getRandomTask()
        );

        verify(tasksRepository).findRandom();
        verify(taskOptionsRepository, never()).findByTaskId(TASK_ID);
        verify(taskErrorItemsRepository, never()).findByTaskId(TASK_ID);
    }

    private static void assertBaseTaskResponse(
            @Nonnull GetRandomTaskResponse response,
            String taskType,
            boolean autoCheckEnabled
    ) {
        assertEquals(TASK_ID, response.id());
        assertEquals(TRAINER_ID, response.trainerId());
        assertEquals(TRAINER_TITLE, response.trainerTitle());
        assertEquals(taskType, response.taskType());
        assertEquals(TITLE, response.title());
        assertEquals(DESCRIPTION, response.description());
        assertEquals(CONTENT, response.content());
        assertEquals(MAX_SCORE, response.maxScore());
        assertEquals(autoCheckEnabled, response.autoCheckEnabled());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());
    }

    private static FindTaskDetailsByIdResponseEntity taskEntity(
            String taskType,
            boolean autoCheckEnabled
    ) {
        return FindTaskDetailsByIdResponseEntity.builder()
                .id(TASK_ID)
                .trainerId(TRAINER_ID)
                .trainerTitle(TRAINER_TITLE)
                .taskType(taskType)
                .title(TITLE)
                .description(DESCRIPTION)
                .content(CONTENT)
                .maxScore(MAX_SCORE)
                .autoCheckEnabled(autoCheckEnabled)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static FindTaskOptionsByTaskIdResponseEntity firstOptionEntity() {
        return FindTaskOptionsByTaskIdResponseEntity.builder()
                .id(FIRST_OPTION_ID)
                .optionText(FIRST_OPTION_TEXT)
                .build();
    }

    private static FindTaskOptionsByTaskIdResponseEntity secondOptionEntity() {
        return FindTaskOptionsByTaskIdResponseEntity.builder()
                .id(SECOND_OPTION_ID)
                .optionText(SECOND_OPTION_TEXT)
                .build();
    }

    private static FindTaskErrorItemsByTaskIdResponseEntity firstErrorItemEntity() {
        return FindTaskErrorItemsByTaskIdResponseEntity.builder()
                .id(FIRST_ERROR_ITEM_ID)
                .fragmentText(FIRST_FRAGMENT_TEXT)
                .build();
    }

    private static FindTaskErrorItemsByTaskIdResponseEntity secondErrorItemEntity() {
        return FindTaskErrorItemsByTaskIdResponseEntity.builder()
                .id(SECOND_ERROR_ITEM_ID)
                .fragmentText(SECOND_FRAGMENT_TEXT)
                .build();
    }

}
