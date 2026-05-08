package com.example.repositories;

import com.example.admin.entities.requests.CreateTrainerEntityRequest;
import com.example.admin.entities.response.CreateTrainerEntityResponse;
import com.example.admin.exceptions.TrainerWasNotCreatedException;
import com.example.trainers.entities.responses.TrainerEntityResponse;
import com.example.trainers.entities.responses.TrainerInfoByIdEntityResponse;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;

import static com.example.jooq.generated.tables.Trainers.TRAINERS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TrainersRepository {

    DSLContext dsl;

    public List<TrainerEntityResponse> getAllTrainers() {
        return dsl.selectFrom(TRAINERS)
                .fetch(response -> TrainerEntityResponse.builder()
                        .id(response.getId())
                        .title(response.getTitle())
                        .description(response.getDescription())
                        .difficultyLevel(response.getDifficultyLevel())
                        .isActive(response.getIsActive())
                        .createdAt(response.getCreatedAt())
                        .updatedAt(response.getUpdatedAt())
                        .build()
                );
    }

    public Optional<TrainerInfoByIdEntityResponse> getTrainerInfoById(long id) {
        return dsl.selectFrom(TRAINERS)
                .where(TRAINERS.ID.eq(id))
                .fetchOptional(response -> TrainerInfoByIdEntityResponse.builder()
                        .id(response.getId())
                        .title(response.getTitle())
                        .description(response.getDescription())
                        .difficultyLevel(response.getDifficultyLevel())
                        .isActive(response.getIsActive())
                        .createdAt(response.getCreatedAt())
                        .updatedAt(response.getUpdatedAt())
                        .build()
                );
    }

    public CreateTrainerEntityResponse createTrainer(@Nonnull CreateTrainerEntityRequest request) {
        return dsl.insertInto(TRAINERS)
                .set(TRAINERS.TITLE, request.title())
                .set(TRAINERS.DESCRIPTION, request.description())
                .set(TRAINERS.DIFFICULTY_LEVEL, request.difficultyLevel())
                .set(TRAINERS.IS_ACTIVE, request.isActive())
                .returning(
                        TRAINERS.ID,
                        TRAINERS.TITLE,
                        TRAINERS.DESCRIPTION,
                        TRAINERS.DIFFICULTY_LEVEL,
                        TRAINERS.IS_ACTIVE,
                        TRAINERS.CREATED_AT,
                        TRAINERS.UPDATED_AT
                )
                .fetchOptional()
                .map(record -> CreateTrainerEntityResponse.builder()
                        .id(record.get(TRAINERS.ID))
                        .title(record.get(TRAINERS.TITLE))
                        .description(record.get(TRAINERS.DESCRIPTION))
                        .difficultyLevel(record.get(TRAINERS.DIFFICULTY_LEVEL))
                        .isActive(record.get(TRAINERS.IS_ACTIVE))
                        .createdAt(record.get(TRAINERS.CREATED_AT))
                        .updatedAt(record.get(TRAINERS.UPDATED_AT))
                        .build()
                )
                .orElseThrow(TrainerWasNotCreatedException::new);
    }

    public boolean existsActiveById(Long trainerId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(TRAINERS)
                        .where(TRAINERS.ID.eq(trainerId))
                        .and(TRAINERS.IS_ACTIVE.isTrue())
        );
    }

}
