package com.example.admin.services;

import com.example.admin.dto.requests.UpdateTrainerRequest;
import com.example.admin.dto.responses.UpdateTrainerResponse;
import com.example.admin.entities.requests.UpdateTrainerRequestEntity;
import com.example.admin.entities.response.UpdateTrainerResponseEntity;
import com.example.repositories.TrainersRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UpdateTrainerService {

    TrainersRepository trainersRepository;

    public UpdateTrainerResponse updateTrainer(Long id, @Nonnull UpdateTrainerRequest request) {
        UpdateTrainerRequestEntity entityRequest = UpdateTrainerRequestEntity.builder()
                .id(id)
                .title(request.title())
                .description(request.description())
                .difficultyLevel(request.difficultyLevel())
                .isActive(request.isActive())
                .build();

        UpdateTrainerResponseEntity trainer = trainersRepository.update(entityRequest)
                .orElseThrow(() -> new TrainerNotFoundException(id));

        return mapToDtoResponse(trainer);
    }

    private static UpdateTrainerResponse mapToDtoResponse(@Nonnull UpdateTrainerResponseEntity entity) {
        return UpdateTrainerResponse.builder()
                .id(entity.id())
                .title(entity.title())
                .description(entity.description())
                .difficultyLevel(entity.difficultyLevel())
                .isActive(entity.isActive())
                .createdAt(entity.createdAt())
                .updatedAt(entity.updatedAt())
                .build();
    }

}
