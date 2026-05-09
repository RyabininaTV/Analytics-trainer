package com.example.repositories;

import com.example.admin.entities.requests.CreateTaskRequestEntity;
import com.example.admin.entities.requests.UpdateTaskRequestEntity;
import com.example.admin.entities.response.CreateTaskResponseEntity;
import com.example.admin.entities.response.DeactivateTaskResponseEntity;
import com.example.admin.entities.response.UpdateTaskResponseEntity;
import com.example.jooq.generated.enums.TaskTypeEnum;
import com.example.trainers.entities.responses.TaskByTrainerIdEntityResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Optional;

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

    public CreateTaskResponseEntity create(@Nonnull CreateTaskRequestEntity request) {
        return dsl.insertInto(TASKS)
                .set(TASKS.TRAINER_ID, request.trainerId())
                .set(TASKS.TASK_TYPE, TaskTypeEnum.valueOf(request.taskType()))
                .set(TASKS.TITLE, request.title())
                .set(TASKS.DESCRIPTION, request.description())
                .set(TASKS.CONTENT, request.content())
                .set(TASKS.MAX_SCORE, request.maxScore())
                .set(TASKS.IS_ACTIVE, request.isActive())
                .set(TASKS.AUTO_CHECK_ENABLED, request.autoCheckEnabled())
                .returning(
                        TASKS.ID,
                        TASKS.TRAINER_ID,
                        TASKS.TASK_TYPE,
                        TASKS.TITLE,
                        TASKS.DESCRIPTION,
                        TASKS.CONTENT,
                        TASKS.MAX_SCORE,
                        TASKS.IS_ACTIVE,
                        TASKS.AUTO_CHECK_ENABLED,
                        TASKS.CREATED_AT,
                        TASKS.UPDATED_AT
                )
                .fetchOptional()
                .map(record -> CreateTaskResponseEntity.builder()
                        .id(record.get(TASKS.ID))
                        .trainerId(record.get(TASKS.TRAINER_ID))
                        .taskType(record.get(TASKS.TASK_TYPE).name())
                        .title(record.get(TASKS.TITLE))
                        .description(record.get(TASKS.DESCRIPTION))
                        .content(record.get(TASKS.CONTENT))
                        .maxScore(record.get(TASKS.MAX_SCORE))
                        .isActive(record.get(TASKS.IS_ACTIVE))
                        .autoCheckEnabled(record.get(TASKS.AUTO_CHECK_ENABLED))
                        .createdAt(record.get(TASKS.CREATED_AT))
                        .updatedAt(record.get(TASKS.UPDATED_AT))
                        .build()
                )
                .orElseThrow();
    }

    public Optional<UpdateTaskResponseEntity> update(@Nonnull UpdateTaskRequestEntity request) {
        return dsl.update(TASKS)
                .set(TASKS.TRAINER_ID, request.trainerId())
                .set(TASKS.TASK_TYPE, TaskTypeEnum.valueOf(request.taskType()))
                .set(TASKS.TITLE, request.title())
                .set(TASKS.DESCRIPTION, request.description())
                .set(TASKS.CONTENT, request.content())
                .set(TASKS.MAX_SCORE, request.maxScore())
                .set(TASKS.IS_ACTIVE, request.isActive())
                .set(TASKS.AUTO_CHECK_ENABLED, request.autoCheckEnabled())
                .set(TASKS.UPDATED_AT, DSL.currentLocalDateTime())
                .where(TASKS.ID.eq(request.id()))
                .returning(
                        TASKS.ID,
                        TASKS.TRAINER_ID,
                        TASKS.TASK_TYPE,
                        TASKS.TITLE,
                        TASKS.DESCRIPTION,
                        TASKS.CONTENT,
                        TASKS.MAX_SCORE,
                        TASKS.IS_ACTIVE,
                        TASKS.AUTO_CHECK_ENABLED,
                        TASKS.CREATED_AT,
                        TASKS.UPDATED_AT
                )
                .fetchOptional(record -> UpdateTaskResponseEntity.builder()
                        .id(record.get(TASKS.ID))
                        .trainerId(record.get(TASKS.TRAINER_ID))
                        .taskType(record.get(TASKS.TASK_TYPE).name())
                        .title(record.get(TASKS.TITLE))
                        .description(record.get(TASKS.DESCRIPTION))
                        .content(record.get(TASKS.CONTENT))
                        .maxScore(record.get(TASKS.MAX_SCORE))
                        .isActive(record.get(TASKS.IS_ACTIVE))
                        .autoCheckEnabled(record.get(TASKS.AUTO_CHECK_ENABLED))
                        .createdAt(record.get(TASKS.CREATED_AT))
                        .updatedAt(record.get(TASKS.UPDATED_AT))
                        .build()
                );
    }

    public void deactivateById(Long id) {
        dsl.update(TASKS)
                .set(TASKS.IS_ACTIVE, false)
                .set(TASKS.UPDATED_AT, DSL.currentLocalDateTime())
                .where(TASKS.ID.eq(id))
                .execute();
    }

}
