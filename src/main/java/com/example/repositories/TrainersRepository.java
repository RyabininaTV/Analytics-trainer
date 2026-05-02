package com.example.repositories;

import com.example.trainers.entities.responses.TrainerEntityResponse;
import com.example.trainers.entities.responses.TrainerInfoByIdEntityResponse;
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

}
