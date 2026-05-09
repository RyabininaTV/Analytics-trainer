package com.example.repositories;

import com.example.admin.entities.requests.CreateTaskErrorItemRequestEntity;
import com.example.admin.entities.response.CreateTaskErrorItemResponseEntity;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.List;

import static com.example.jooq.generated.tables.TaskErrorItems.TASK_ERROR_ITEMS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskErrorItemsRepository {

    DSLContext dsl;

    public List<CreateTaskErrorItemResponseEntity> create(@Nonnull List<CreateTaskErrorItemRequestEntity> requests) {
        return requests.stream()
                .map(this::create)
                .toList();
    }

    @Nonnull
    private CreateTaskErrorItemResponseEntity create(@Nonnull CreateTaskErrorItemRequestEntity request) {
        return dsl.insertInto(TASK_ERROR_ITEMS)
                .set(TASK_ERROR_ITEMS.TASK_ID, request.taskId())
                .set(TASK_ERROR_ITEMS.FRAGMENT_TEXT, request.fragmentText())
                .set(TASK_ERROR_ITEMS.IS_ERROR, request.isError())
                .set(TASK_ERROR_ITEMS.EXPLANATION, request.explanation())
                .returning(
                        TASK_ERROR_ITEMS.ID,
                        TASK_ERROR_ITEMS.FRAGMENT_TEXT,
                        TASK_ERROR_ITEMS.IS_ERROR,
                        TASK_ERROR_ITEMS.EXPLANATION
                )
                .fetchOptional()
                .map(record -> CreateTaskErrorItemResponseEntity.builder()
                        .id(record.get(TASK_ERROR_ITEMS.ID))
                        .fragmentText(record.get(TASK_ERROR_ITEMS.FRAGMENT_TEXT))
                        .isError(record.get(TASK_ERROR_ITEMS.IS_ERROR))
                        .explanation(record.get(TASK_ERROR_ITEMS.EXPLANATION))
                        .build()
                )
                .orElseThrow();
    }

    public void deleteByTaskId(Long taskId) {
        dsl.deleteFrom(TASK_ERROR_ITEMS)
                .where(TASK_ERROR_ITEMS.TASK_ID.eq(taskId))
                .execute();
    }

}
