package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.FindCheckedAttemptsByUserIdResponseEntity;
import com.example.progress_and_profile.dto.responses.ProgressHistoryItemResponse;
import com.example.repositories.AttemptsRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetHistoryOfUserProgressChangesServiceTest {

    private static final Long USER_ID = 10L;

    private static final Long FIRST_TASK_ID = 100L;
    private static final Long SECOND_TASK_ID = 200L;

    private static final Integer FIRST_SCORE = 5;
    private static final Integer SECOND_SCORE = 8;
    private static final Integer LOWER_SCORE = 3;
    private static final Integer SAME_SCORE = 8;
    private static final Integer THIRD_SCORE = 10;

    private static final Integer FIRST_TOTAL_SCORE = 5;
    private static final Integer SECOND_TOTAL_SCORE = 13;
    private static final Integer THIRD_TOTAL_SCORE = 18;

    private static final LocalDateTime FIRST_CHANGED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime SECOND_CHANGED_AT = LocalDateTime.of(2026, 1, 1, 12, 10);
    private static final LocalDateTime LOWER_CHANGED_AT = LocalDateTime.of(2026, 1, 1, 12, 20);
    private static final LocalDateTime SAME_CHANGED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);
    private static final LocalDateTime THIRD_CHANGED_AT = LocalDateTime.of(2026, 1, 1, 12, 40);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    AttemptsRepository attemptsRepository;

    @InjectMocks
    GetHistoryOfUserProgressChangesService getHistoryOfUserProgressChangesService;

    @Test
    void getHistoryOfUserProgressChanges_shouldReturnProgressHistory() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.findCheckedAttemptsByUserId(USER_ID))
                .thenReturn(List.of(
                        attemptEntity(FIRST_TASK_ID, FIRST_SCORE, FIRST_CHANGED_AT),
                        attemptEntity(SECOND_TASK_ID, SECOND_SCORE, SECOND_CHANGED_AT),
                        attemptEntity(FIRST_TASK_ID, LOWER_SCORE, LOWER_CHANGED_AT),
                        attemptEntity(SECOND_TASK_ID, SAME_SCORE, SAME_CHANGED_AT),
                        attemptEntity(FIRST_TASK_ID, THIRD_SCORE, THIRD_CHANGED_AT)
                ));

        List<ProgressHistoryItemResponse> response = getHistoryOfUserProgressChangesService
                .getHistoryOfUserProgressChanges();

        assertEquals(3, response.size());

        ProgressHistoryItemResponse firstHistoryItem = response.getFirst();

        assertEquals(FIRST_CHANGED_AT, firstHistoryItem.changedAt());
        assertEquals(FIRST_TOTAL_SCORE, firstHistoryItem.totalScore());

        ProgressHistoryItemResponse secondHistoryItem = response.get(1);

        assertEquals(SECOND_CHANGED_AT, secondHistoryItem.changedAt());
        assertEquals(SECOND_TOTAL_SCORE, secondHistoryItem.totalScore());

        ProgressHistoryItemResponse thirdHistoryItem = response.get(2);

        assertEquals(THIRD_CHANGED_AT, thirdHistoryItem.changedAt());
        assertEquals(THIRD_TOTAL_SCORE, thirdHistoryItem.totalScore());

        verify(attemptsRepository).findCheckedAttemptsByUserId(USER_ID);
    }

    @Test
    void getHistoryOfUserProgressChanges_shouldReturnEmptyList_whenUserHasNoCheckedAttempts() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.findCheckedAttemptsByUserId(USER_ID))
                .thenReturn(List.of());

        List<ProgressHistoryItemResponse> response = getHistoryOfUserProgressChangesService
                .getHistoryOfUserProgressChanges();

        assertEquals(0, response.size());

        verify(attemptsRepository).findCheckedAttemptsByUserId(USER_ID);
    }

    private static FindCheckedAttemptsByUserIdResponseEntity attemptEntity(
            Long taskId,
            Integer score,
            LocalDateTime changedAt
    ) {
        return FindCheckedAttemptsByUserIdResponseEntity.builder()
                .taskId(taskId)
                .score(score)
                .changedAt(changedAt)
                .build();
    }

}
