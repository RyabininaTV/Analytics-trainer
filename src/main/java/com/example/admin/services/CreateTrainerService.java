package com.example.admin.services;

import com.example.admin.dto.requests.CreateTrainerRequest;
import com.example.admin.dto.responses.CreateTrainerResponse;
import com.example.admin.entities.requests.CreateTrainerEntityRequest;
import com.example.admin.entities.response.CreateTrainerEntityResponse;
import com.example.repositories.TrainersRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateTrainerService {

    private final TrainersRepository trainerRepository;

    @Transactional
    public CreateTrainerResponse createTrainer(@Nonnull CreateTrainerRequest request) {
        CreateTrainerEntityResponse response = trainerRepository.createTrainer(CreateTrainerEntityRequest.builder()
                .title(request.title())
                .description(request.description())
                .difficultyLevel(request.difficultyLevel())
                .isActive(request.isActive())
                .build()
        );

        return CreateTrainerResponse.builder()
                .id(response.id())
                .title(response.title())
                .description(response.description())
                .isActive(response.isActive())
                .createdAt(response.createdAt())
                .updatedAt(response.updatedAt())
                .build();
    }
}