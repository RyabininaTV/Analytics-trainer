package com.example.trainers.services;

import com.example.repositories.TrainersRepository;
import com.example.trainers.dto.responses.TrainerInfoByIdResponse;
import com.example.trainers.entities.responses.TrainerInfoByIdEntityResponse;
import com.example.trainers.exceptions.TrainerNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTrainerInfoByIdServiceTest {

    private static final long TRAINER_ID = 1L;

    private static final String TITLE = "SQL trainer";
    private static final String DESCRIPTION = "SQL tasks";

    private static final String DIFFICULTY_LEVEL = "difficulty level";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 4, 25, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 4, 25, 11, 0);

    @Mock
    TrainersRepository trainersRepository;

    @Nested
    class GetTrainerInfoByIdTest {

        @Test
        void trainerExists_shouldReturnTrainerInfoByIdResponse() {
            when(trainersRepository.getTrainerInfoById(TRAINER_ID))
                    .thenReturn(Optional.of(trainer()));

            GetTrainerInfoByIdService service = new GetTrainerInfoByIdService(trainersRepository);

            TrainerInfoByIdResponse response = service.getTrainerInfoById(TRAINER_ID);

            assertNotNull(response);
            assertEquals(TRAINER_ID, response.id());
            assertEquals(TITLE, response.title());
            assertEquals(DESCRIPTION, response.description());
            assertEquals(DIFFICULTY_LEVEL, response.difficultyLevel());
            assertTrue(response.isActive());
            assertEquals(CREATED_AT, response.createdAt());
            assertEquals(UPDATED_AT, response.updatedAt());

            verify(trainersRepository).getTrainerInfoById(TRAINER_ID);
            verifyNoMoreInteractions(trainersRepository);
        }

        @Test
        void trainerDoesNotExist_shouldThrowTrainerNotFoundException() {
            when(trainersRepository.getTrainerInfoById(TRAINER_ID))
                    .thenReturn(Optional.empty());

            GetTrainerInfoByIdService service = new GetTrainerInfoByIdService(trainersRepository);

            assertThrows(
                    TrainerNotFoundException.class,
                    () -> service.getTrainerInfoById(TRAINER_ID)
            );

            verify(trainersRepository).getTrainerInfoById(TRAINER_ID);
            verifyNoMoreInteractions(trainersRepository);
        }

    }

    private static TrainerInfoByIdEntityResponse trainer() {
        return TrainerInfoByIdEntityResponse.builder()
                .id(TRAINER_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .difficultyLevel(DIFFICULTY_LEVEL)
                .isActive(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
