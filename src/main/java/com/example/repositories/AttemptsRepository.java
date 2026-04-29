package com.example.repositories;

import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import static com.example.jooq.generated.tables.Attempts.ATTEMPTS;
import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AttemptsRepository {

    DSLContext dsl;

    public void deleteUserAttemptsByTrainerId(@Nonnull DeleteUserAttemptsByTrainerIdEntityRequest request) {
        dsl.deleteFrom(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(request.userId()))
                .and(ATTEMPTS.TASK_ID.in(
                        dsl.select(TASKS.ID)
                                .from(TASKS)
                                .where(TASKS.TRAINER_ID.eq(request.trainerId()))
                ))
                .execute();
    }

}
