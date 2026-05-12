package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProgressItemResponse;
import com.example.progress_and_profile.entity.responses.FindUserProgressByUserIdResponseEntity;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProgressByAllTrainersServiceTest {

    private static final Long USER_ID = 10L;

    private static final Long FIRST_TRAINER_ID = 100L;
    private static final Long SECOND_TRAINER_ID = 200L;

    private static final Integer FIRST_COMPLETED_TASKS_COUNT = 5;
    private static final Integer SECOND_COMPLETED_TASKS_COUNT = 8;

    private static final Integer FIRST_TOTAL_TASKS_COUNT = 10;
    private static final Integer SECOND_TOTAL_TASKS_COUNT = 16;

    private static final Integer FIRST_TOTAL_SCORE = 50;
    private static final Integer SECOND_TOTAL_SCORE = 80;

    private static final BigDecimal FIRST_COMPLETION_PERCENT = BigDecimal.valueOf(50);
    private static final BigDecimal SECOND_COMPLETION_PERCENT = BigDecimal.valueOf(75);

    private static final LocalDateTime FIRST_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime SECOND_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 13, 0);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    UserProgressRepository userProgressRepository;

    @InjectMocks
    GetUserProgressByAllTrainersService getUserProgressByAllTrainersService;

    @Test
    void getUserProgressByAllTrainers_shouldReturnUserProgressByAllTrainers() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userProgressRepository.findByUserId(USER_ID))
                .thenReturn(List.of(
                        firstProgressEntity(),
                        secondProgressEntity()
                ));

        List<ProgressItemResponse> response = getUserProgressByAllTrainersService
                .getUserProgressByAllTrainers();

        assertEquals(2, response.size());

        ProgressItemResponse firstProgressItem = response.getFirst();

        assertEquals(FIRST_TRAINER_ID, firstProgressItem.trainerId());
        assertEquals(FIRST_COMPLETED_TASKS_COUNT, firstProgressItem.completedTasksCount());
        assertEquals(FIRST_TOTAL_TASKS_COUNT, firstProgressItem.totalTasksCount());
        assertEquals(FIRST_TOTAL_SCORE, firstProgressItem.totalScore());
        assertEquals(FIRST_COMPLETION_PERCENT, firstProgressItem.completionPercent());
        assertEquals(FIRST_LAST_ACTIVITY_AT, firstProgressItem.lastActivityAt());

        ProgressItemResponse secondProgressItem = response.get(1);

        assertEquals(SECOND_TRAINER_ID, secondProgressItem.trainerId());
        assertEquals(SECOND_COMPLETED_TASKS_COUNT, secondProgressItem.completedTasksCount());
        assertEquals(SECOND_TOTAL_TASKS_COUNT, secondProgressItem.totalTasksCount());
        assertEquals(SECOND_TOTAL_SCORE, secondProgressItem.totalScore());
        assertEquals(SECOND_COMPLETION_PERCENT, secondProgressItem.completionPercent());
        assertEquals(SECOND_LAST_ACTIVITY_AT, secondProgressItem.lastActivityAt());

        verify(userProgressRepository).findByUserId(USER_ID);
    }

    @Test
    void getUserProgressByAllTrainers_shouldReturnEmptyList_whenUserHasNoProgress() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userProgressRepository.findByUserId(USER_ID))
                .thenReturn(List.of());

        List<ProgressItemResponse> response = getUserProgressByAllTrainersService
                .getUserProgressByAllTrainers();

        assertEquals(0, response.size());

        verify(userProgressRepository).findByUserId(USER_ID);
    }

    private static FindUserProgressByUserIdResponseEntity firstProgressEntity() {
        return FindUserProgressByUserIdResponseEntity.builder()
                .trainerId(FIRST_TRAINER_ID)
                .completedTasksCount(FIRST_COMPLETED_TASKS_COUNT)
                .totalTasksCount(FIRST_TOTAL_TASKS_COUNT)
                .totalScore(FIRST_TOTAL_SCORE)
                .completionPercent(FIRST_COMPLETION_PERCENT)
                .lastActivityAt(FIRST_LAST_ACTIVITY_AT)
                .build();
    }

    private static FindUserProgressByUserIdResponseEntity secondProgressEntity() {
        return FindUserProgressByUserIdResponseEntity.builder()
                .trainerId(SECOND_TRAINER_ID)
                .completedTasksCount(SECOND_COMPLETED_TASKS_COUNT)
                .totalTasksCount(SECOND_TOTAL_TASKS_COUNT)
                .totalScore(SECOND_TOTAL_SCORE)
                .completionPercent(SECOND_COMPLETION_PERCENT)
                .lastActivityAt(SECOND_LAST_ACTIVITY_AT)
                .build();
    }

}
