package com.example.admin.services;

import com.example.admin.dto.requests.CreateTrainerRequest;
import com.example.admin.dto.responses.CreateTrainerResponse;
import com.example.admin.entities.requests.CreateTrainerEntityRequest;
import com.example.admin.entities.response.CreateTrainerEntityResponse;
import com.example.repositories.TrainersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTrainerServiceTest {

    private static final Long TRAINER_ID = 10L;

    private static final String TITLE = "SQL тренажёр";
    private static final String DESCRIPTION = "Тренажёр для аналитиков";
    private static final String DIFFICULTY_LEVEL = "EASY";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    TrainersRepository trainerRepository;

    @InjectMocks
    CreateTrainerService createTrainerService;

    @Test
    void createTrainer_shouldCreateTrainer() {
        CreateTrainerRequest request = createTrainerRequest();

        when(trainerRepository.createTrainer(org.mockito.ArgumentMatchers.any(CreateTrainerEntityRequest.class)))
                .thenReturn(createTrainerEntityResponse());

        CreateTrainerResponse response = createTrainerService.createTrainer(request);

        assertEquals(TRAINER_ID, response.id());
        assertEquals(TITLE, response.title());
        assertEquals(DESCRIPTION, response.description());
        assertTrue(response.isActive());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals(UPDATED_AT, response.updatedAt());

        ArgumentCaptor<CreateTrainerEntityRequest> captor =
                ArgumentCaptor.forClass(CreateTrainerEntityRequest.class);

        verify(trainerRepository).createTrainer(captor.capture());

        CreateTrainerEntityRequest entityRequest = captor.getValue();

        assertEquals(TITLE, entityRequest.title());
        assertEquals(DESCRIPTION, entityRequest.description());
        assertEquals(DIFFICULTY_LEVEL, entityRequest.difficultyLevel());
        assertTrue(entityRequest.isActive());
    }

    private static CreateTrainerRequest createTrainerRequest() {
        return CreateTrainerRequest.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .difficultyLevel(DIFFICULTY_LEVEL)
                .isActive(true)
                .build();
    }

    private static CreateTrainerEntityResponse createTrainerEntityResponse() {
        return CreateTrainerEntityResponse.builder()
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
