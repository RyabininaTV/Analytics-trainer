package com.example.repositories;

import com.example.progress_and_profile.dto.responses.FindCheckedAttemptsByUserIdResponseEntity;
import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.jooq.generated.enums.AttemptStatusEnum.CHECKED;
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

    public List<FindCheckedAttemptsByUserIdResponseEntity> findCheckedAttemptsByUserId(@Nonnull Long userId) {
        Field<LocalDateTime> changedAt = DSL.coalesce(ATTEMPTS.REVIEWED_AT, ATTEMPTS.SUBMITTED_AT);

        return dsl.select(
                        ATTEMPTS.TASK_ID,
                        changedAt,
                        ATTEMPTS.SCORE
                )
                .from(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(userId))
                .and(ATTEMPTS.STATUS.eq(CHECKED))
                .and(ATTEMPTS.SCORE.isNotNull())
                .and(changedAt.isNotNull())
                .orderBy(changedAt.asc(), ATTEMPTS.ID.asc())
                .fetch(record -> FindCheckedAttemptsByUserIdResponseEntity.builder()
                        .taskId(record.get(ATTEMPTS.TASK_ID))
                        .changedAt(record.get(changedAt))
                        .score(record.get(ATTEMPTS.SCORE))
                        .build()
                );
    }

}
