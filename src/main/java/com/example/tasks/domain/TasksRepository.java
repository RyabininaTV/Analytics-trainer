package com.example.tasks.domain;

import com.example.tasks.domain.model.TaskDetailsResponse;
import com.example.tasks.domain.model.TaskResponse;

import java.util.List;
import java.util.Optional;

public interface TasksRepository {
    List<TaskResponse> getAllTasks();
    Optional<TaskDetailsResponse> getTaskDetailsById(long id);
    Optional<TaskDetailsResponse> getTaskDetailsByRandomId();
}
