package com.example.task;

import com.example.jooq.generated.enums.TaskTypeEnum;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.*;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;
import static org.jooq.impl.DSL.rand;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class TasksRepository {
    DSLContext dsl;

    public @Nonnull Stream<Record4<Long, Long, TaskTypeEnum, String>> getAll() {
        return dsl.select(
                        TASKS.ID,
                        TASKS.SIMULATOR_ID,
                        TASKS.TASK_TYPE,
                        TASKS.TITLE
                )
                .from(TASKS)
                .where(TASKS.IS_ACTIVE)
                .fetchStream();
    }

    public @Nonnull SelectConditionStep<Record8<Long, Long, TaskTypeEnum, String, String, String, Integer, Boolean>> getTask(long id) {
        return selectDetailsFromTasks()
                .where(TASKS.ID.eq(id).and(TASKS.IS_ACTIVE));
    }

    public @Nonnull SelectSeekStep1<Record8<Long, Long, TaskTypeEnum, String, String, String, Integer, Boolean>, BigDecimal> getRandom() {
        return selectDetailsFromTasks()
                .where(TASKS.IS_ACTIVE)
                .orderBy(rand());
    }

    private @Nonnull SelectJoinStep<Record8<Long, Long, TaskTypeEnum, String, String, String, Integer, Boolean>> selectDetailsFromTasks() {
        return dsl.select(
                        TASKS.ID,
                        TASKS.SIMULATOR_ID,
                        TASKS.TASK_TYPE,
                        TASKS.TITLE,
                        TASKS.DESCRIPTION,
                        TASKS.CONTENT,
                        TASKS.MAX_SCORE,
                        TASKS.AUTO_CHECK_ENABLED
                )
                .from(TASKS);
    }

}