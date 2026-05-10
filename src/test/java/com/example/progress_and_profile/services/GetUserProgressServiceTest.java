package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProgressResponse;
import com.example.progress_and_profile.entity.responses.FindTotalUserProgressResponseEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProgressServiceTest {

    private static final Long USER_ID = 10L;

    private static final Integer COMPLETED_TASKS_COUNT = 5;
    private static final Integer TOTAL_TASKS_COUNT = 10;
    private static final Integer TOTAL_SCORE = 80;

    private static final Integer ROUNDING_COMPLETED_TASKS_COUNT = 1;
    private static final Integer ROUNDING_TOTAL_TASKS_COUNT = 3;
    private static final Integer ROUNDING_TOTAL_SCORE = 20;

    private static final Integer ZERO_COMPLETED_TASKS_COUNT = 0;
    private static final Integer ZERO_TOTAL_TASKS_COUNT = 0;
    private static final Integer ZERO_TOTAL_SCORE = 0;

    private static final BigDecimal COMPLETION_PERCENT = new BigDecimal("50.00");
    private static final BigDecimal ROUNDING_COMPLETION_PERCENT = new BigDecimal("33.33");
    private static final BigDecimal ZERO_COMPLETION_PERCENT = BigDecimal.ZERO;

    private static final LocalDateTime LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    UserProgressRepository userProgressRepository;

    @InjectMocks
    GetUserProgressService getUserProgressService;

    @Test
    void getUserProgress_shouldReturnUserProgress() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userProgressRepository.findTotalByUserId(USER_ID))
                .thenReturn(progressEntity(
                        COMPLETED_TASKS_COUNT,
                        TOTAL_TASKS_COUNT,
                        TOTAL_SCORE,
                        LAST_ACTIVITY_AT
                ));

        ProgressResponse response = getUserProgressService.getUserProgress();

        assertEquals(COMPLETED_TASKS_COUNT, response.completedTasksCount());
        assertEquals(TOTAL_TASKS_COUNT, response.totalTasksCount());
        assertEquals(TOTAL_SCORE, response.totalScore());
        assertEquals(COMPLETION_PERCENT, response.completionPercent());
        assertEquals(LAST_ACTIVITY_AT, response.lastActivityAt());

        verify(userProgressRepository).findTotalByUserId(USER_ID);
    }

    @Test
    void getUserProgress_shouldRoundCompletionPercentHalfUp() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userProgressRepository.findTotalByUserId(USER_ID))
                .thenReturn(progressEntity(
                        ROUNDING_COMPLETED_TASKS_COUNT,
                        ROUNDING_TOTAL_TASKS_COUNT,
                        ROUNDING_TOTAL_SCORE,
                        LAST_ACTIVITY_AT
                ));

        ProgressResponse response = getUserProgressService.getUserProgress();

        assertEquals(ROUNDING_COMPLETION_PERCENT, response.completionPercent());

        verify(userProgressRepository).findTotalByUserId(USER_ID);
    }

    @Test
    void getUserProgress_shouldReturnZeroCompletionPercent_whenTotalTasksCountIsZero() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(userProgressRepository.findTotalByUserId(USER_ID))
                .thenReturn(progressEntity(
                        ZERO_COMPLETED_TASKS_COUNT,
                        ZERO_TOTAL_TASKS_COUNT,
                        ZERO_TOTAL_SCORE,
                        null
                ));

        ProgressResponse response = getUserProgressService.getUserProgress();

        assertEquals(ZERO_COMPLETED_TASKS_COUNT, response.completedTasksCount());
        assertEquals(ZERO_TOTAL_TASKS_COUNT, response.totalTasksCount());
        assertEquals(ZERO_TOTAL_SCORE, response.totalScore());
        assertEquals(ZERO_COMPLETION_PERCENT, response.completionPercent());
        assertNull(response.lastActivityAt());

        verify(userProgressRepository).findTotalByUserId(USER_ID);
    }

    private static FindTotalUserProgressResponseEntity progressEntity(
            Integer completedTasksCount,
            Integer totalTasksCount,
            Integer totalScore,
            LocalDateTime lastActivityAt
    ) {
        return FindTotalUserProgressResponseEntity.builder()
                .completedTasksCount(completedTasksCount)
                .totalTasksCount(totalTasksCount)
                .totalScore(totalScore)
                .lastActivityAt(lastActivityAt)
                .build();
    }

}
