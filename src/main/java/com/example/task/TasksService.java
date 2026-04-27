package com.example.task;

import com.example.task.model.BasicTask;
import com.example.task.model.TaskDetails;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class TasksService {

    private final TasksRepository tasksRepository;

    @Nonnull
    public List<BasicTask> getAll() {
        return tasksRepository.getAll().map(Mapper::toBasicTask).toList();
    }

    @Nonnull
    public Optional<TaskDetails> getTask(long id) {
        return tasksRepository.getTask(id).fetchOptional(Mapper::toTaskDetails);
    }

    @Nonnull
    public Optional<TaskDetails> getRandom() {
        return tasksRepository.getRandom().fetchOptional(Mapper::toTaskDetails);
    }
}