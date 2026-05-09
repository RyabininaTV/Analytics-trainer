package com.example.tasks.domain.service;

import com.example.tasks.domain.TasksRepository;
import com.example.tasks.domain.exception.TaskNotFoundException;
import com.example.tasks.domain.model.TaskDetailsResponse;
import com.example.tasks.domain.model.TaskResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksService {

    TasksRepository tasksRepository;

    @Nonnull
    public List<TaskResponse> getAllTasks() {
        return tasksRepository.getAllTasks();
    }

    @Nonnull
    public TaskDetailsResponse getTaskDetailsById(long id) {
        return tasksRepository.getTaskDetailsById(id).orElseThrow(() -> {
            log.error("Task not found with id: {}", id);
            return new TaskNotFoundException();
        });
    }

    @Nonnull
    public TaskDetailsResponse getTaskDetailsByRandomId() {
        return tasksRepository.getTaskDetailsByRandomId().orElseThrow(() -> {
            log.error("Task not found with random id");
            return new TaskNotFoundException();
        });
    }
}