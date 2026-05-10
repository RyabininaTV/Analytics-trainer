package com.example.admin.services;

import com.example.admin.dto.requests.UpdateTrainerRequest;
import com.example.admin.dto.responses.UpdateTrainerResponse;
import com.example.admin.entities.requests.UpdateTrainerRequestEntity;
import com.example.admin.entities.response.UpdateTrainerResponseEntity;
import com.example.repositories.TrainersRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTrainerServiceTest {

    private static final Long TRAINER_ID = 10L;

    private static final String TITLE = "SQL тренажёр";
    private static final String DESCRIPTION = "Тренажёр для аналитиков";
    private static final String DIFFICULTY_LEVEL = "EASY";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    TrainersRepository trainersRepository;

    @InjectMocks
    UpdateTrainerService updateTrainerService;

    @Test
    void updateTrainer_shouldUpdateTrainer() {
        UpdateTrainerRequest request = updateTrainerRequest();

        when(trainersRepository.update(any(UpdateTrainerRequestEntity.class)))
                .thenReturn(Optional.of(updateTrainerResponseEntity()));

        UpdateTrainerResponse response = updateTrainerService.updateTrainer(TRAINER_ID, request);

        assertEquals(TRAINER_ID, response.id());
        assertEquals(TITLE, response.title());
        assertEquals(DESCRIPTION, response.description());
        assertEquals(DIFFICULTY_LEVEL, response.difficultyLevel());
        assertTrue(response.isActive());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());

        ArgumentCaptor<UpdateTrainerRequestEntity> captor =
                ArgumentCaptor.forClass(UpdateTrainerRequestEntity.class);

        verify(trainersRepository).update(captor.capture());

        UpdateTrainerRequestEntity entityRequest = captor.getValue();

        assertEquals(TRAINER_ID, entityRequest.id());
        assertEquals(TITLE, entityRequest.title());
        assertEquals(DESCRIPTION, entityRequest.description());
        assertEquals(DIFFICULTY_LEVEL, entityRequest.difficultyLevel());
        assertTrue(entityRequest.isActive());
    }

    @Test
    void updateTrainer_shouldThrowTrainerNotFoundException_whenTrainerDoesNotExist() {
        UpdateTrainerRequest request = updateTrainerRequest();

        when(trainersRepository.update(any(UpdateTrainerRequestEntity.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> updateTrainerService.updateTrainer(TRAINER_ID, request)
        );

        verify(trainersRepository).update(any(UpdateTrainerRequestEntity.class));
    }

    private static UpdateTrainerRequest updateTrainerRequest() {
        return UpdateTrainerRequest.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .difficultyLevel(DIFFICULTY_LEVEL)
                .isActive(true)
                .build();
    }

    private static UpdateTrainerResponseEntity updateTrainerResponseEntity() {
        return UpdateTrainerResponseEntity.builder()
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
