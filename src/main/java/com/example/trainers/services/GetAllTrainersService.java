package com.example.trainers.services;

import com.example.repositories.TrainersRepository;
import com.example.trainers.dto.responses.TrainerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetAllTrainersService {

    TrainersRepository trainersRepository;

    public List<TrainerResponse> getAllTrainers() {
        return trainersRepository.getAllTrainers()
                .stream()
                .map(entity -> TrainerResponse.builder()
                        .id(entity.id())
                        .title(entity.title())
                        .description(entity.description())
                        .difficultyLevel(entity.difficultyLevel())
                        .isActive(entity.isActive())
                        .createdAt(entity.createdAt())
                        .updatedAt(entity.updatedAt())
                        .build()
                )
                .toList();
    }

}
