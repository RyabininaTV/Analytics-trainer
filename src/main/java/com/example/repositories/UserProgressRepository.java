package com.example.repositories;

import com.example.progress_and_profile.entity.responses.FindTotalUserProgressResponseEntity;
import com.example.progress_and_profile.entity.responses.FindUserProgressByUserIdResponseEntity;
import com.example.trainers.entities.requests.DeleteUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.requests.GetUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.responses.UserProgressByTrainerIdEntityResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.jooq.generated.tables.UserProgress.USER_PROGRESS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserProgressRepository {

    DSLContext dsl;

    public Optional<UserProgressByTrainerIdEntityResponse> getUserProgressByTrainerId(
            @Nonnull GetUserProgressByTrainerIdEntityRequest request
    ) {
        return dsl.selectFrom(USER_PROGRESS)
                .where(USER_PROGRESS.USER_ID.eq(request.userId()))
                .and(USER_PROGRESS.TRAINER_ID.eq(request.trainerId()))
                .fetchOptional(response -> UserProgressByTrainerIdEntityResponse.builder()
                        .id(response.getId())
                        .userId(response.getUserId())
                        .trainerId(response.getTrainerId())
                        .completedTasksCount(response.getCompletedTasksCount())
                        .totalTasksCount(response.getTotalTasksCount())
                        .totalScore(response.getTotalScore())
                        .completionPercent(response.getCompletionPercent())
                        .lastActivityAt(response.getLastActivityAt())
                        .build()
                );
    }

    public void deleteUserProgressByTrainerId(@Nonnull DeleteUserProgressByTrainerIdEntityRequest request) {
        dsl.deleteFrom(USER_PROGRESS)
                .where(USER_PROGRESS.USER_ID.eq(request.userId()))
                .and(USER_PROGRESS.TRAINER_ID.eq(request.trainerId()))
                .execute();
    }

    public List<FindUserProgressByUserIdResponseEntity> findByUserId(@Nonnull Long userId) {
        return dsl.select(
                        USER_PROGRESS.TRAINER_ID,
                        USER_PROGRESS.COMPLETED_TASKS_COUNT,
                        USER_PROGRESS.TOTAL_TASKS_COUNT,
                        USER_PROGRESS.TOTAL_SCORE,
                        USER_PROGRESS.COMPLETION_PERCENT,
                        USER_PROGRESS.LAST_ACTIVITY_AT
                )
                .from(USER_PROGRESS)
                .where(USER_PROGRESS.USER_ID.eq(userId))
                .orderBy(
                        USER_PROGRESS.LAST_ACTIVITY_AT.desc().nullsLast(),
                        USER_PROGRESS.TRAINER_ID.asc()
                )
                .fetch(record -> FindUserProgressByUserIdResponseEntity.builder()
                        .trainerId(record.get(USER_PROGRESS.TRAINER_ID))
                        .completedTasksCount(record.get(USER_PROGRESS.COMPLETED_TASKS_COUNT))
                        .totalTasksCount(record.get(USER_PROGRESS.TOTAL_TASKS_COUNT))
                        .totalScore(record.get(USER_PROGRESS.TOTAL_SCORE))
                        .completionPercent(record.get(USER_PROGRESS.COMPLETION_PERCENT))
                        .lastActivityAt(record.get(USER_PROGRESS.LAST_ACTIVITY_AT))
                        .build()
                );
    }

    public FindTotalUserProgressResponseEntity findTotalByUserId(Long userId) {
        Field<Integer> completedTasksCount = coalesceInteger(DSL.sum(USER_PROGRESS.COMPLETED_TASKS_COUNT));
        Field<Integer> totalTasksCount = coalesceInteger(DSL.sum(USER_PROGRESS.TOTAL_TASKS_COUNT));
        Field<Integer> totalScore = coalesceInteger(DSL.sum(USER_PROGRESS.TOTAL_SCORE));
        Field<LocalDateTime> lastActivityAt = DSL.max(USER_PROGRESS.LAST_ACTIVITY_AT);

        return dsl.select(
                        completedTasksCount,
                        totalTasksCount,
                        totalScore,
                        lastActivityAt
                )
                .from(USER_PROGRESS)
                .where(USER_PROGRESS.USER_ID.eq(userId))
                .fetchOne(record -> FindTotalUserProgressResponseEntity.builder()
                        .completedTasksCount(record.get(completedTasksCount))
                        .totalTasksCount(record.get(totalTasksCount))
                        .totalScore(record.get(totalScore))
                        .lastActivityAt(record.get(lastActivityAt))
                        .build()
                );
    }

    @Nonnull
    private static Field<Integer> coalesceInteger(AggregateFunction<BigDecimal> field) {
        return DSL.coalesce(field, BigDecimal.ZERO).cast(Integer.class);
    }

}
