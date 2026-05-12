package com.example.tasks.services;

import com.example.repositories.TasksRepository;
import com.example.tasks.dto.responses.GetTaskItemResponse;
import com.example.tasks.entity.requests.FindTasksRequestEntity;
import com.example.tasks.entity.responses.FindTasksResponseEntity;
import com.example.tasks.service.GetTasksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTasksServiceTest {

    private static final Long TRAINER_ID = 10L;

    private static final Long FIRST_TASK_ID = 100L;
    private static final Long SECOND_TASK_ID = 200L;

    private static final String TRAINER_TITLE = "SQL тренажёр";

    private static final String TEST_TASK_TYPE = "TEST";
    private static final String OPEN_TASK_TYPE = "OPEN";

    private static final String FIRST_TITLE = "Первое задание";
    private static final String SECOND_TITLE = "Второе задание";

    private static final String FIRST_DESCRIPTION = "Описание первого задания";
    private static final String SECOND_DESCRIPTION = "Описание второго задания";

    private static final String FIRST_CONTENT = "Контент первого задания";
    private static final String SECOND_CONTENT = "Контент второго задания";

    private static final Integer FIRST_MAX_SCORE = 10;
    private static final Integer SECOND_MAX_SCORE = 20;

    private static final Boolean FIRST_AUTO_CHECK_ENABLED = Boolean.TRUE;
    private static final Boolean SECOND_AUTO_CHECK_ENABLED = Boolean.FALSE;

    private static final LocalDateTime FIRST_CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime FIRST_UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    private static final LocalDateTime SECOND_CREATED_AT = LocalDateTime.of(2026, 1, 2, 12, 0);
    private static final LocalDateTime SECOND_UPDATED_AT = LocalDateTime.of(2026, 1, 2, 12, 30);

    @Mock
    TasksRepository tasksRepository;

    @InjectMocks
    GetTasksService getTasksService;

    @Test
    void getTasks_shouldReturnTasks() {
        when(tasksRepository.findTasks(any(FindTasksRequestEntity.class)))
                .thenReturn(List.of(
                        firstTaskEntity(),
                        secondTaskEntity()
                ));

        List<GetTaskItemResponse> response = getTasksService.getTasks(TRAINER_ID);

        assertEquals(2, response.size());

        GetTaskItemResponse firstTask = response.getFirst();

        assertEquals(FIRST_TASK_ID, firstTask.id());
        assertEquals(TRAINER_ID, firstTask.trainerId());
        assertEquals(TRAINER_TITLE, firstTask.trainerTitle());
        assertEquals(TEST_TASK_TYPE, firstTask.taskType());
        assertEquals(FIRST_TITLE, firstTask.title());
        assertEquals(FIRST_DESCRIPTION, firstTask.description());
        assertEquals(FIRST_CONTENT, firstTask.content());
        assertEquals(FIRST_MAX_SCORE, firstTask.maxScore());
        assertEquals(FIRST_AUTO_CHECK_ENABLED, firstTask.autoCheckEnabled());
        assertEquals(FIRST_CREATED_AT, firstTask.createdAt());
        assertEquals(FIRST_UPDATED_AT, firstTask.updatedAt());

        GetTaskItemResponse secondTask = response.get(1);

        assertEquals(SECOND_TASK_ID, secondTask.id());
        assertEquals(TRAINER_ID, secondTask.trainerId());
        assertEquals(TRAINER_TITLE, secondTask.trainerTitle());
        assertEquals(OPEN_TASK_TYPE, secondTask.taskType());
        assertEquals(SECOND_TITLE, secondTask.title());
        assertEquals(SECOND_DESCRIPTION, secondTask.description());
        assertEquals(SECOND_CONTENT, secondTask.content());
        assertEquals(SECOND_MAX_SCORE, secondTask.maxScore());
        assertEquals(SECOND_AUTO_CHECK_ENABLED, secondTask.autoCheckEnabled());
        assertEquals(SECOND_CREATED_AT, secondTask.createdAt());
        assertEquals(SECOND_UPDATED_AT, secondTask.updatedAt());

        ArgumentCaptor<FindTasksRequestEntity> requestCaptor =
                ArgumentCaptor.forClass(FindTasksRequestEntity.class);

        verify(tasksRepository).findTasks(requestCaptor.capture());

        FindTasksRequestEntity request = requestCaptor.getValue();

        assertEquals(TRAINER_ID, request.trainerId());
    }

    @Test
    void getTasks_shouldReturnEmptyList_whenTasksDoNotExist() {
        when(tasksRepository.findTasks(any(FindTasksRequestEntity.class)))
                .thenReturn(List.of());

        List<GetTaskItemResponse> response = getTasksService.getTasks(TRAINER_ID);

        assertEquals(0, response.size());

        ArgumentCaptor<FindTasksRequestEntity> requestCaptor =
                ArgumentCaptor.forClass(FindTasksRequestEntity.class);

        verify(tasksRepository).findTasks(requestCaptor.capture());

        FindTasksRequestEntity request = requestCaptor.getValue();

        assertEquals(TRAINER_ID, request.trainerId());
    }

    private static FindTasksResponseEntity firstTaskEntity() {
        return FindTasksResponseEntity.builder()
                .id(FIRST_TASK_ID)
                .trainerId(TRAINER_ID)
                .trainerTitle(TRAINER_TITLE)
                .taskType(TEST_TASK_TYPE)
                .title(FIRST_TITLE)
                .description(FIRST_DESCRIPTION)
                .content(FIRST_CONTENT)
                .maxScore(FIRST_MAX_SCORE)
                .autoCheckEnabled(FIRST_AUTO_CHECK_ENABLED)
                .createdAt(FIRST_CREATED_AT)
                .updatedAt(FIRST_UPDATED_AT)
                .build();
    }

    private static FindTasksResponseEntity secondTaskEntity() {
        return FindTasksResponseEntity.builder()
                .id(SECOND_TASK_ID)
                .trainerId(TRAINER_ID)
                .trainerTitle(TRAINER_TITLE)
                .taskType(OPEN_TASK_TYPE)
                .title(SECOND_TITLE)
                .description(SECOND_DESCRIPTION)
                .content(SECOND_CONTENT)
                .maxScore(SECOND_MAX_SCORE)
                .autoCheckEnabled(SECOND_AUTO_CHECK_ENABLED)
                .createdAt(SECOND_CREATED_AT)
                .updatedAt(SECOND_UPDATED_AT)
                .build();
    }

}
