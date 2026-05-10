package com.example.ratings_and_achievements.servises;

import com.example.ratings_and_achievements.dto.responses.LeaderboardItemResponse;
import com.example.ratings_and_achievements.entity.responses.FindLeaderboardByTrainerIdResponseEntity;
import com.example.ratings_and_achievements.service.GetLeaderboardByTrainerService;
import com.example.repositories.TrainersRepository;
import com.example.repositories.UserProgressRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLeaderboardByTrainerServiceTest {

    private static final Long TRAINER_ID = 100L;

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;

    private static final String FIRST_USERNAME = "first-user";
    private static final String SECOND_USERNAME = "second-user";

    private static final Integer FIRST_POSITION = 1;
    private static final Integer SECOND_POSITION = 2;

    private static final Integer FIRST_COMPLETED_TASKS_COUNT = 10;
    private static final Integer SECOND_COMPLETED_TASKS_COUNT = 8;

    private static final Integer TOTAL_TASKS_COUNT = 10;

    private static final Integer FIRST_TOTAL_SCORE = 100;
    private static final Integer SECOND_TOTAL_SCORE = 80;

    private static final BigDecimal FIRST_COMPLETION_PERCENT = new BigDecimal("100.00");
    private static final BigDecimal SECOND_COMPLETION_PERCENT = new BigDecimal("80.00");

    private static final LocalDateTime FIRST_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime SECOND_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 13, 0);

    @Mock
    TrainersRepository trainersRepository;

    @Mock
    UserProgressRepository userProgressRepository;

    @InjectMocks
    GetLeaderboardByTrainerService getLeaderboardByTrainerService;

    @Test
    void getLeaderboardByTrainer_shouldReturnLeaderboard() {
        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(userProgressRepository.findLeaderboardByTrainerId(TRAINER_ID))
                .thenReturn(List.of(
                        firstLeaderboardEntity(),
                        secondLeaderboardEntity()
                ));

        List<LeaderboardItemResponse> response = getLeaderboardByTrainerService
                .getLeaderboardByTrainer(TRAINER_ID);

        assertEquals(2, response.size());

        LeaderboardItemResponse firstItem = response.getFirst();

        assertEquals(FIRST_POSITION, firstItem.position());
        assertEquals(FIRST_USER_ID, firstItem.userId());
        assertEquals(FIRST_USERNAME, firstItem.username());
        assertEquals(FIRST_COMPLETED_TASKS_COUNT, firstItem.completedTasksCount());
        assertEquals(TOTAL_TASKS_COUNT, firstItem.totalTasksCount());
        assertEquals(FIRST_TOTAL_SCORE, firstItem.totalScore());
        assertEquals(FIRST_COMPLETION_PERCENT, firstItem.completionPercent());
        assertEquals(FIRST_LAST_ACTIVITY_AT, firstItem.lastActivityAt());

        LeaderboardItemResponse secondItem = response.get(1);

        assertEquals(SECOND_POSITION, secondItem.position());
        assertEquals(SECOND_USER_ID, secondItem.userId());
        assertEquals(SECOND_USERNAME, secondItem.username());
        assertEquals(SECOND_COMPLETED_TASKS_COUNT, secondItem.completedTasksCount());
        assertEquals(TOTAL_TASKS_COUNT, secondItem.totalTasksCount());
        assertEquals(SECOND_TOTAL_SCORE, secondItem.totalScore());
        assertEquals(SECOND_COMPLETION_PERCENT, secondItem.completionPercent());
        assertEquals(SECOND_LAST_ACTIVITY_AT, secondItem.lastActivityAt());

        verify(trainersRepository).existsActiveById(TRAINER_ID);
        verify(userProgressRepository).findLeaderboardByTrainerId(TRAINER_ID);
    }

    @Test
    void getLeaderboardByTrainer_shouldReturnEmptyList_whenLeaderboardIsEmpty() {
        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(true);

        when(userProgressRepository.findLeaderboardByTrainerId(TRAINER_ID))
                .thenReturn(List.of());

        List<LeaderboardItemResponse> response = getLeaderboardByTrainerService
                .getLeaderboardByTrainer(TRAINER_ID);

        assertEquals(0, response.size());

        verify(trainersRepository).existsActiveById(TRAINER_ID);
        verify(userProgressRepository).findLeaderboardByTrainerId(TRAINER_ID);
    }

    @Test
    void getLeaderboardByTrainer_shouldThrowTrainerNotFoundException_whenTrainerDoesNotExist() {
        when(trainersRepository.existsActiveById(TRAINER_ID))
                .thenReturn(false);

        assertThrows(
                TrainerNotFoundException.class,
                () -> getLeaderboardByTrainerService.getLeaderboardByTrainer(TRAINER_ID)
        );

        verify(trainersRepository).existsActiveById(TRAINER_ID);
        verify(userProgressRepository, never()).findLeaderboardByTrainerId(TRAINER_ID);
    }

    private static FindLeaderboardByTrainerIdResponseEntity firstLeaderboardEntity() {
        return FindLeaderboardByTrainerIdResponseEntity.builder()
                .userId(FIRST_USER_ID)
                .username(FIRST_USERNAME)
                .completedTasksCount(FIRST_COMPLETED_TASKS_COUNT)
                .totalTasksCount(TOTAL_TASKS_COUNT)
                .totalScore(FIRST_TOTAL_SCORE)
                .completionPercent(FIRST_COMPLETION_PERCENT)
                .lastActivityAt(FIRST_LAST_ACTIVITY_AT)
                .build();
    }

    private static FindLeaderboardByTrainerIdResponseEntity secondLeaderboardEntity() {
        return FindLeaderboardByTrainerIdResponseEntity.builder()
                .userId(SECOND_USER_ID)
                .username(SECOND_USERNAME)
                .completedTasksCount(SECOND_COMPLETED_TASKS_COUNT)
                .totalTasksCount(TOTAL_TASKS_COUNT)
                .totalScore(SECOND_TOTAL_SCORE)
                .completionPercent(SECOND_COMPLETION_PERCENT)
                .lastActivityAt(SECOND_LAST_ACTIVITY_AT)
                .build();
    }

}
