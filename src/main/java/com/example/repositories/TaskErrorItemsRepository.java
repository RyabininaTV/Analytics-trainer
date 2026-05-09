package com.example.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.Optional;

import static com.example.jooq.generated.tables.TaskErrorItems.TASK_ERROR_ITEMS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskErrorItemsRepository {

    DSLContext dsl;

    public Optional<String> findFragmentTextById(Long errorItemId) {
        return dsl.select(TASK_ERROR_ITEMS.FRAGMENT_TEXT)
                .from(TASK_ERROR_ITEMS)
                .where(TASK_ERROR_ITEMS.ID.eq(errorItemId))
                .fetchOptional(TASK_ERROR_ITEMS.FRAGMENT_TEXT);
    }
}
