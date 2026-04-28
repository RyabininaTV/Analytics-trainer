package com.example.task.domain;

import com.example.task.domain.model.TaskDetailsResponse;
import com.example.task.domain.model.TaskResponse;

import java.util.List;
import java.util.Optional;

public interface TasksRepository {
    List<TaskResponse> getAllTasks();
    Optional<TaskDetailsResponse> getTaskDetailsById(long id);
    Optional<TaskDetailsResponse> getTaskDetailsByRandomId();
}
