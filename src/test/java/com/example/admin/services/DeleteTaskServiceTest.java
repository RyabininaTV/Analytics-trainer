package com.example.admin.services;

import com.example.repositories.TasksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceTest {

    private static final Long TASK_ID = 100L;

    @Mock
    TasksRepository tasksRepository;

    @InjectMocks
    DeleteTaskService deleteTaskService;

    @Test
    void deleteTask_shouldDeactivateTaskById() {
        deleteTaskService.deleteTask(TASK_ID);

        verify(tasksRepository).deactivateById(TASK_ID);
        verifyNoMoreInteractions(tasksRepository);
    }

}
