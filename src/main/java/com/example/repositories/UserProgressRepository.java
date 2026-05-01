package com.example.repositories;

import com.example.trainers.entities.requests.DeleteUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.requests.GetUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.responses.UserProgressByTrainerIdEntityResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

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

}
