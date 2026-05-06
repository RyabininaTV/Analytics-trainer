package com.example.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.Optional;

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
}
