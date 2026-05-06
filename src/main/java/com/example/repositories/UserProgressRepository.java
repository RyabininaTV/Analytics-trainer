package com.example.repositories;

import com.example.attempts.entities.requests.UpdateUserProgressEntityRequest;
import com.example.trainers.entities.requests.DeleteUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.requests.GetUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.responses.UserProgressByTrainerIdEntityResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
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

    @Transactional
    public void updateProgress(@Nonnull UpdateUserProgressEntityRequest request) {
        var existing = dsl.selectFrom(USER_PROGRESS)
                .where(USER_PROGRESS.USER_ID.eq(request.userId()))
                .and(USER_PROGRESS.TRAINER_ID.eq(request.trainerId()))
                .fetchOptional();

        if (existing.isPresent()) {
            dsl.update(USER_PROGRESS)
                    .set(USER_PROGRESS.COMPLETED_TASKS_COUNT, request.completedTasksCount())
                    .set(USER_PROGRESS.TOTAL_TASKS_COUNT, request.totalTasksCount())
                    .set(USER_PROGRESS.TOTAL_SCORE, request.totalScore())
                    .set(USER_PROGRESS.COMPLETION_PERCENT, request.completionPercent())
                    .set(USER_PROGRESS.LAST_ACTIVITY_AT, LocalDateTime.now())
                    .where(USER_PROGRESS.USER_ID.eq(request.userId()))
                    .and(USER_PROGRESS.TRAINER_ID.eq(request.trainerId()))
                    .execute();
        } else {
            dsl.insertInto(USER_PROGRESS)
                    .set(USER_PROGRESS.USER_ID, request.userId())
                    .set(USER_PROGRESS.TRAINER_ID, request.trainerId())
                    .set(USER_PROGRESS.COMPLETED_TASKS_COUNT, request.completedTasksCount())
                    .set(USER_PROGRESS.TOTAL_TASKS_COUNT, request.totalTasksCount())
                    .set(USER_PROGRESS.TOTAL_SCORE, request.totalScore())
                    .set(USER_PROGRESS.COMPLETION_PERCENT, request.completionPercent())
                    .set(USER_PROGRESS.LAST_ACTIVITY_AT, LocalDateTime.now())
                    .execute();
        }
    }
}
