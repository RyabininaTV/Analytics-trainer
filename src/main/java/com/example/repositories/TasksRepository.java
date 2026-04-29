package com.example.repositories;

import com.example.trainers.entities.responses.TaskByTrainerIdEntityResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.List;

import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksRepository {

    DSLContext dsl;

    public List<TaskByTrainerIdEntityResponse> getTasksByTrainerId(long trainerId) {
        return dsl.selectFrom(TASKS)
                .where(TASKS.TRAINER_ID.eq(trainerId))
                .fetch(response -> TaskByTrainerIdEntityResponse.builder()
                        .id(response.getId())
                        .trainerId(response.getTrainerId())
                        .taskType(response.getTaskType())
                        .title(response.getTitle())
                        .description(response.getDescription())
                        .content(response.getContent())
                        .maxScore(response.getMaxScore())
                        .isActive(response.getIsActive())
                        .autoCheckEnabled(response.getAutoCheckEnabled())
                        .createdAt(response.getCreatedAt())
                        .updatedAt(response.getUpdatedAt())
                        .build()
                );
    }

}
