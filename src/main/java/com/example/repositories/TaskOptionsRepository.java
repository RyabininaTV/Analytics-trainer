package com.example.repositories;

import com.example.admin.entities.requests.CreateTaskOptionRequestEntity;
import com.example.admin.entities.response.CreateTaskOptionResponseEntity;
import com.example.tasks.entity.responses.FindTaskOptionsByTaskIdResponseEntity;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.Optional;
import java.util.List;

import static com.example.jooq.generated.tables.TaskOptions.TASK_OPTIONS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskOptionsRepository {

    DSLContext dsl;

    public Optional<String> findOptionTextById(Long optionId) {
        return dsl.select(TASK_OPTIONS.OPTION_TEXT)
                .from(TASK_OPTIONS)
                .where(TASK_OPTIONS.ID.eq(optionId))
                .fetchOptional(TASK_OPTIONS.OPTION_TEXT);
    }

    public List<CreateTaskOptionResponseEntity> create(@Nonnull List<CreateTaskOptionRequestEntity> requests) {
        return requests.stream()
                .map(this::create)
                .toList();
    }

    @Nonnull
    private CreateTaskOptionResponseEntity create(@Nonnull CreateTaskOptionRequestEntity request) {
        return dsl.insertInto(TASK_OPTIONS)
                .set(TASK_OPTIONS.TASK_ID, request.taskId())
                .set(TASK_OPTIONS.OPTION_TEXT, request.optionText())
                .set(TASK_OPTIONS.IS_CORRECT, request.isCorrect())
                .returning(
                        TASK_OPTIONS.ID,
                        TASK_OPTIONS.OPTION_TEXT,
                        TASK_OPTIONS.IS_CORRECT
                )
                .fetchOptional()
                .map(record -> CreateTaskOptionResponseEntity.builder()
                        .id(record.get(TASK_OPTIONS.ID))
                        .optionText(record.get(TASK_OPTIONS.OPTION_TEXT))
                        .isCorrect(record.get(TASK_OPTIONS.IS_CORRECT))
                        .build()
                )
                .orElseThrow();
    }

    public void deleteByTaskId(Long taskId) {
        dsl.deleteFrom(TASK_OPTIONS)
                .where(TASK_OPTIONS.TASK_ID.eq(taskId))
                .execute();
    }

    public List<FindTaskOptionsByTaskIdResponseEntity> findByTaskId(Long taskId) {
        return dsl.select(
                        TASK_OPTIONS.ID,
                        TASK_OPTIONS.OPTION_TEXT
                )
                .from(TASK_OPTIONS)
                .where(TASK_OPTIONS.TASK_ID.eq(taskId))
                .orderBy(TASK_OPTIONS.ID.asc())
                .fetch(record -> FindTaskOptionsByTaskIdResponseEntity.builder()
                        .id(record.get(TASK_OPTIONS.ID))
                        .optionText(record.get(TASK_OPTIONS.OPTION_TEXT))
                        .build()
                );
    }

}
