package com.example.trainers.services;

import com.example.repositories.TrainersRepository;
import com.example.trainers.dto.responses.TrainerInfoByIdResponse;
import com.example.trainers.entities.responses.TrainerInfoByIdEntityResponse;
import com.example.trainers.exceptions.TrainerNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetTrainerInfoByIdService {

    TrainersRepository trainersRepository;

    public TrainerInfoByIdResponse getTrainerInfoById(long id) {
        TrainerInfoByIdEntityResponse entity = trainersRepository.getTrainerInfoById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));

        return TrainerInfoByIdResponse.builder()
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
