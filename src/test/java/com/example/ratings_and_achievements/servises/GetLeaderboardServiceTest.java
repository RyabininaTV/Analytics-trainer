package com.example.ratings_and_achievements.servises;

import com.example.ratings_and_achievements.dto.responses.LeaderboardItemResponse;
import com.example.ratings_and_achievements.entity.responses.FindLeaderboardUsersResponseEntity;
import com.example.ratings_and_achievements.service.GetLeaderboardService;
import com.example.repositories.UserRepository;
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
class GetLeaderboardServiceTest {

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;
    private static final Long THIRD_USER_ID = 3L;

    private static final String FIRST_USERNAME = "first-user";
    private static final String SECOND_USERNAME = "second-user";
    private static final String THIRD_USERNAME = "third-user";

    private static final Integer FIRST_POSITION = 1;
    private static final Integer SECOND_POSITION = 2;
    private static final Integer THIRD_POSITION = 3;

    private static final Integer FIRST_COMPLETED_TASKS_COUNT = 10;
    private static final Integer SECOND_COMPLETED_TASKS_COUNT = 1;
    private static final Integer THIRD_COMPLETED_TASKS_COUNT = 0;

    private static final Integer FIRST_TOTAL_TASKS_COUNT = 10;
    private static final Integer SECOND_TOTAL_TASKS_COUNT = 3;
    private static final Integer THIRD_TOTAL_TASKS_COUNT = 0;

    private static final Integer FIRST_TOTAL_SCORE = 100;
    private static final Integer SECOND_TOTAL_SCORE = 30;
    private static final Integer THIRD_TOTAL_SCORE = 0;

    private static final BigDecimal FIRST_COMPLETION_PERCENT = new BigDecimal("100.00");
    private static final BigDecimal SECOND_COMPLETION_PERCENT = new BigDecimal("33.33");
    private static final BigDecimal THIRD_COMPLETION_PERCENT = BigDecimal.ZERO;

    private static final LocalDateTime FIRST_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime SECOND_LAST_ACTIVITY_AT = LocalDateTime.of(2026, 1, 1, 13, 0);
    private static final LocalDateTime THIRD_LAST_ACTIVITY_AT = null;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    GetLeaderboardService getLeaderboardService;

    @Test
    void getLeaderboard_shouldReturnLeaderboard() {
        when(userRepository.findLeaderboardUsers())
                .thenReturn(List.of(
                        firstLeaderboardUserEntity(),
                        secondLeaderboardUserEntity(),
                        thirdLeaderboardUserEntity()
                ));

        List<LeaderboardItemResponse> response = getLeaderboardService.getLeaderboard();

        assertEquals(3, response.size());

        LeaderboardItemResponse firstItem = response.getFirst();

        assertEquals(FIRST_POSITION, firstItem.position());
        assertEquals(FIRST_USER_ID, firstItem.userId());
        assertEquals(FIRST_USERNAME, firstItem.username());
        assertEquals(FIRST_COMPLETED_TASKS_COUNT, firstItem.completedTasksCount());
        assertEquals(FIRST_TOTAL_TASKS_COUNT, firstItem.totalTasksCount());
        assertEquals(FIRST_TOTAL_SCORE, firstItem.totalScore());
        assertEquals(FIRST_COMPLETION_PERCENT, firstItem.completionPercent());
        assertEquals(FIRST_LAST_ACTIVITY_AT, firstItem.lastActivityAt());

        LeaderboardItemResponse secondItem = response.get(1);

        assertEquals(SECOND_POSITION, secondItem.position());
        assertEquals(SECOND_USER_ID, secondItem.userId());
        assertEquals(SECOND_USERNAME, secondItem.username());
        assertEquals(SECOND_COMPLETED_TASKS_COUNT, secondItem.completedTasksCount());
        assertEquals(SECOND_TOTAL_TASKS_COUNT, secondItem.totalTasksCount());
        assertEquals(SECOND_TOTAL_SCORE, secondItem.totalScore());
        assertEquals(SECOND_COMPLETION_PERCENT, secondItem.completionPercent());
        assertEquals(SECOND_LAST_ACTIVITY_AT, secondItem.lastActivityAt());

        LeaderboardItemResponse thirdItem = response.get(2);

        assertEquals(THIRD_POSITION, thirdItem.position());
        assertEquals(THIRD_USER_ID, thirdItem.userId());
        assertEquals(THIRD_USERNAME, thirdItem.username());
        assertEquals(THIRD_COMPLETED_TASKS_COUNT, thirdItem.completedTasksCount());
        assertEquals(THIRD_TOTAL_TASKS_COUNT, thirdItem.totalTasksCount());
        assertEquals(THIRD_TOTAL_SCORE, thirdItem.totalScore());
        assertEquals(THIRD_COMPLETION_PERCENT, thirdItem.completionPercent());
        assertEquals(THIRD_LAST_ACTIVITY_AT, thirdItem.lastActivityAt());

        verify(userRepository).findLeaderboardUsers();
    }

    @Test
    void getLeaderboard_shouldReturnEmptyList_whenLeaderboardIsEmpty() {
        when(userRepository.findLeaderboardUsers())
                .thenReturn(List.of());

        List<LeaderboardItemResponse> response = getLeaderboardService.getLeaderboard();

        assertEquals(0, response.size());

        verify(userRepository).findLeaderboardUsers();
    }

    private static FindLeaderboardUsersResponseEntity firstLeaderboardUserEntity() {
        return FindLeaderboardUsersResponseEntity.builder()
                .userId(FIRST_USER_ID)
                .username(FIRST_USERNAME)
                .completedTasksCount(FIRST_COMPLETED_TASKS_COUNT)
                .totalTasksCount(FIRST_TOTAL_TASKS_COUNT)
                .totalScore(FIRST_TOTAL_SCORE)
                .lastActivityAt(FIRST_LAST_ACTIVITY_AT)
                .build();
    }

    private static FindLeaderboardUsersResponseEntity secondLeaderboardUserEntity() {
        return FindLeaderboardUsersResponseEntity.builder()
                .userId(SECOND_USER_ID)
                .username(SECOND_USERNAME)
                .completedTasksCount(SECOND_COMPLETED_TASKS_COUNT)
                .totalTasksCount(SECOND_TOTAL_TASKS_COUNT)
                .totalScore(SECOND_TOTAL_SCORE)
                .lastActivityAt(SECOND_LAST_ACTIVITY_AT)
                .build();
    }

    private static FindLeaderboardUsersResponseEntity thirdLeaderboardUserEntity() {
        return FindLeaderboardUsersResponseEntity.builder()
                .userId(THIRD_USER_ID)
                .username(THIRD_USERNAME)
                .completedTasksCount(THIRD_COMPLETED_TASKS_COUNT)
                .totalTasksCount(THIRD_TOTAL_TASKS_COUNT)
                .totalScore(THIRD_TOTAL_SCORE)
                .lastActivityAt(THIRD_LAST_ACTIVITY_AT)
                .build();
    }

}
