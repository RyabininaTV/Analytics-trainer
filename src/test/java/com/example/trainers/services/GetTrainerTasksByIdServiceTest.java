package com.example.trainers.services;

import com.example.repositories.TasksRepository;
import com.example.trainers.dto.responses.TaskByTrainerIdResponse;
import com.example.trainers.entities.responses.TaskByTrainerIdEntityResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.jooq.generated.enums.TaskTypeEnum.OPEN;
import static com.example.jooq.generated.enums.TaskTypeEnum.TEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTrainerTasksByIdServiceTest {

    private static final long TRAINER_ID = 1L;

    private static final Long FIRST_TASK_ID = 10L;
    private static final Long SECOND_TASK_ID = 20L;

    private static final String FIRST_TITLE = "SELECT basics";
    private static final String SECOND_TITLE = "Open SQL question";

    private static final String FIRST_DESCRIPTION = "Choose correct SQL query";
    private static final String SECOND_DESCRIPTION = "Write query manually";

    private static final String FIRST_CONTENT = "What query returns all users?";
    private static final String SECOND_CONTENT = "Write query to find active users";

    private static final Integer FIRST_MAX_SCORE = 5;
    private static final Integer SECOND_MAX_SCORE = 10;

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 4, 25, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 4, 25, 11, 0);

    @Mock
    TasksRepository tasksRepository;

    @Nested
    class GetTrainerTasksByIdTest {

        @Test
        void tasksExist_shouldReturnTaskByTrainerIdResponses() {
            when(tasksRepository.getTasksByTrainerId(TRAINER_ID))
                    .thenReturn(List.of(firstTask(), secondTask()));

            GetTrainerTasksByIdService service = new GetTrainerTasksByIdService(tasksRepository);

            List<TaskByTrainerIdResponse> response = service.getTrainerTasksById(TRAINER_ID);

            assertEquals(2, response.size());

            TaskByTrainerIdResponse first = response.getFirst();
            assertEquals(FIRST_TASK_ID, first.id());
            assertEquals(TRAINER_ID, first.trainerId());
            assertEquals(TEST, first.taskType());
            assertEquals(FIRST_TITLE, first.title());
            assertEquals(FIRST_DESCRIPTION, first.description());
            assertEquals(FIRST_CONTENT, first.content());
            assertEquals(FIRST_MAX_SCORE, first.maxScore());
            assertTrue(first.isActive());
            assertTrue(first.autoCheckEnabled());
            assertEquals(CREATED_AT, first.createdAt());
            assertEquals(UPDATED_AT, first.updatedAt());

            TaskByTrainerIdResponse second = response.get(1);
            assertEquals(SECOND_TASK_ID, second.id());
            assertEquals(TRAINER_ID, second.trainerId());
            assertEquals(OPEN, second.taskType());
            assertEquals(SECOND_TITLE, second.title());
            assertEquals(SECOND_DESCRIPTION, second.description());
            assertEquals(SECOND_CONTENT, second.content());
            assertEquals(SECOND_MAX_SCORE, second.maxScore());
            assertFalse(second.isActive());
            assertFalse(second.autoCheckEnabled());
            assertEquals(CREATED_AT, second.createdAt());
            assertEquals(UPDATED_AT, second.updatedAt());

            verify(tasksRepository).getTasksByTrainerId(TRAINER_ID);
            verifyNoMoreInteractions(tasksRepository);
        }

        @Test
        void tasksDoNotExist_shouldReturnEmptyList() {
            when(tasksRepository.getTasksByTrainerId(TRAINER_ID))
                    .thenReturn(List.of());

            GetTrainerTasksByIdService service = new GetTrainerTasksByIdService(tasksRepository);

            List<TaskByTrainerIdResponse> response = service.getTrainerTasksById(TRAINER_ID);

            assertNotNull(response);
            assertTrue(response.isEmpty());

            verify(tasksRepository).getTasksByTrainerId(TRAINER_ID);
            verifyNoMoreInteractions(tasksRepository);
        }

    }

    private static TaskByTrainerIdEntityResponse firstTask() {
        return TaskByTrainerIdEntityResponse.builder()
                .id(FIRST_TASK_ID)
                .trainerId(TRAINER_ID)
                .taskType(TEST)
                .title(FIRST_TITLE)
                .description(FIRST_DESCRIPTION)
                .content(FIRST_CONTENT)
                .maxScore(FIRST_MAX_SCORE)
                .isActive(true)
                .autoCheckEnabled(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static TaskByTrainerIdEntityResponse secondTask() {
        return TaskByTrainerIdEntityResponse.builder()
                .id(SECOND_TASK_ID)
                .trainerId(TRAINER_ID)
                .taskType(OPEN)
                .title(SECOND_TITLE)
                .description(SECOND_DESCRIPTION)
                .content(SECOND_CONTENT)
                .maxScore(SECOND_MAX_SCORE)
                .isActive(false)
                .autoCheckEnabled(false)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
