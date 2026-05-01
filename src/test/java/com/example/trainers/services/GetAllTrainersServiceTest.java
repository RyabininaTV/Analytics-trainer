package com.example.trainers.services;

import com.example.repositories.TrainersRepository;
import com.example.trainers.dto.responses.TrainerResponse;
import com.example.trainers.entities.responses.TrainerEntityResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllTrainersServiceTest {

    private static final Long FIRST_TRAINER_ID = 1L;
    private static final Long SECOND_TRAINER_ID = 2L;

    private static final String FIRST_TITLE = "SQL trainer";
    private static final String SECOND_TITLE = "Java trainer";

    private static final String FIRST_DESCRIPTION = "SQL tasks";
    private static final String SECOND_DESCRIPTION = "Java tasks";

    private static final String FIRST_DIFFICULTY_LEVEL = "first level";
    private static final String SECOND_DIFFICULTY_LEVEL = "second level";

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 4, 25, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 4, 25, 11, 0);

    @Mock
    TrainersRepository trainersRepository;

    @Nested
    class GetAllTrainersTest {

        @Test
        void trainersExist_shouldReturnTrainerResponses() {
            when(trainersRepository.getAllTrainers())
                    .thenReturn(List.of(firstTrainer(), secondTrainer()));

            GetAllTrainersService service = new GetAllTrainersService(trainersRepository);

            List<TrainerResponse> response = service.getAllTrainers();

            assertEquals(2, response.size());

            TrainerResponse first = response.getFirst();
            assertEquals(FIRST_TRAINER_ID, first.id());
            assertEquals(FIRST_TITLE, first.title());
            assertEquals(FIRST_DESCRIPTION, first.description());
            assertEquals(FIRST_DIFFICULTY_LEVEL, first.difficultyLevel());
            assertTrue(first.isActive());
            assertEquals(CREATED_AT, first.createdAt());
            assertEquals(UPDATED_AT, first.updatedAt());

            TrainerResponse second = response.get(1);
            assertEquals(SECOND_TRAINER_ID, second.id());
            assertEquals(SECOND_TITLE, second.title());
            assertEquals(SECOND_DESCRIPTION, second.description());
            assertEquals(SECOND_DIFFICULTY_LEVEL, second.difficultyLevel());
            assertFalse(second.isActive());
            assertEquals(CREATED_AT, second.createdAt());
            assertEquals(UPDATED_AT, second.updatedAt());

            verify(trainersRepository).getAllTrainers();
            verifyNoMoreInteractions(trainersRepository);
        }

        @Test
        void trainersDoNotExist_shouldReturnEmptyList() {
            when(trainersRepository.getAllTrainers())
                    .thenReturn(List.of());

            GetAllTrainersService service = new GetAllTrainersService(trainersRepository);

            List<TrainerResponse> response = service.getAllTrainers();

            assertNotNull(response);
            assertTrue(response.isEmpty());

            verify(trainersRepository).getAllTrainers();
            verifyNoMoreInteractions(trainersRepository);
        }

    }

    private static TrainerEntityResponse firstTrainer() {
        return TrainerEntityResponse.builder()
                .id(FIRST_TRAINER_ID)
                .title(FIRST_TITLE)
                .description(FIRST_DESCRIPTION)
                .difficultyLevel(FIRST_DIFFICULTY_LEVEL)
                .isActive(true)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    private static TrainerEntityResponse secondTrainer() {
        return TrainerEntityResponse.builder()
                .id(SECOND_TRAINER_ID)
                .title(SECOND_TITLE)
                .description(SECOND_DESCRIPTION)
                .difficultyLevel(SECOND_DIFFICULTY_LEVEL)
                .isActive(false)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
