package com.example.task.data;

import com.example.task.domain.TasksRepository;
import com.example.task.domain.model.TaskDetailsResponse;
import com.example.task.domain.model.TaskResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;

import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;
import static org.jooq.impl.DSL.rand;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksRepositoryImpl implements TasksRepository {
    DSLContext dsl;

    @Override
    public List<TaskResponse> getAllTasks() {
        return dsl.select(
                        TASKS.ID,
                        TASKS.SIMULATOR_ID,
                        TASKS.TASK_TYPE,
                        TASKS.TITLE
                )
                .from(TASKS)
                .where(TASKS.IS_ACTIVE)
                .fetch()
                .map(Mapper::toBasicTask);
    }

    @Override
    public Optional<TaskDetailsResponse> getTaskDetailsById(long id) {
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
                .from(TASKS)
                .where(TASKS.ID.eq(id).and(TASKS.IS_ACTIVE))
                .fetchOptional(Mapper::toTaskDetails);
    }

    @Override
    public Optional<TaskDetailsResponse> getTaskDetailsByRandomId() {
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
                .from(TASKS)
                .where(TASKS.IS_ACTIVE)
                .orderBy(rand())
                .fetchOptional(Mapper::toTaskDetails);
    }
}