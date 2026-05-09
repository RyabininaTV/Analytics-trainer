package com.example.admin.services;

import com.example.repositories.TasksRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeleteTaskService {

    TasksRepository tasksRepository;

    public void deleteTask(@Nonnull Long id) {
        tasksRepository.deactivateById(id);
    }

}
