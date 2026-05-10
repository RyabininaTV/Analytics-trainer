package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptResponse;
import com.example.attempts.entities.responses.AttemptEntityResponse;
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

import static com.example.jooq.generated.enums.AttemptStatusEnum.CHECKED;
import static com.example.jooq.generated.enums.AttemptStatusEnum.SUBMITTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserAttemptsServiceTest {

    private static final Long USER_ID = 10L;

    private static final Long FIRST_ATTEMPT_ID = 1L;
    private static final Long SECOND_ATTEMPT_ID = 2L;

    private static final Long FIRST_TASK_ID = 100L;
    private static final Long SECOND_TASK_ID = 200L;

    private static final Integer FIRST_SCORE = 8;
    private static final Integer SECOND_SCORE = 10;
    private static final Integer MAX_SCORE_SNAPSHOT = 10;

    private static final LocalDateTime FIRST_STARTED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime FIRST_SUBMITTED_AT = LocalDateTime.of(2026, 1, 1, 12, 10);

    private static final LocalDateTime SECOND_STARTED_AT = LocalDateTime.of(2026, 1, 1, 13, 0);
    private static final LocalDateTime SECOND_SUBMITTED_AT = LocalDateTime.of(2026, 1, 1, 13, 10);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    AttemptsRepository attemptsRepository;

    @InjectMocks
    GetUserAttemptsService getUserAttemptsService;

    @Test
    void getUserAttempts_shouldReturnUserAttempts() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getUserAttempts(USER_ID))
                .thenReturn(List.of(
                        firstAttemptEntity(),
                        secondAttemptEntity()
                ));

        List<AttemptResponse> response = getUserAttemptsService.getUserAttempts();

        assertEquals(2, response.size());

        AttemptResponse firstAttempt = response.getFirst();

        assertEquals(FIRST_ATTEMPT_ID, firstAttempt.id());
        assertEquals(FIRST_TASK_ID, firstAttempt.taskId());
        assertEquals(FIRST_STARTED_AT, firstAttempt.startedAt());
        assertEquals(FIRST_SUBMITTED_AT, firstAttempt.submittedAt());
        assertEquals(SUBMITTED, firstAttempt.status());
        assertEquals(FIRST_SCORE, firstAttempt.score());
        assertEquals(MAX_SCORE_SNAPSHOT, firstAttempt.maxScoreSnapshot());
        assertEquals(Boolean.TRUE, firstAttempt.isCorrect());

        AttemptResponse secondAttempt = response.get(1);

        assertEquals(SECOND_ATTEMPT_ID, secondAttempt.id());
        assertEquals(SECOND_TASK_ID, secondAttempt.taskId());
        assertEquals(SECOND_STARTED_AT, secondAttempt.startedAt());
        assertEquals(SECOND_SUBMITTED_AT, secondAttempt.submittedAt());
        assertEquals(CHECKED, secondAttempt.status());
        assertEquals(SECOND_SCORE, secondAttempt.score());
        assertEquals(MAX_SCORE_SNAPSHOT, secondAttempt.maxScoreSnapshot());
        assertEquals(Boolean.FALSE, secondAttempt.isCorrect());

        verify(attemptsRepository).getUserAttempts(USER_ID);
    }

    @Test
    void getUserAttempts_shouldReturnEmptyList_whenUserHasNoAttempts() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getUserAttempts(USER_ID))
                .thenReturn(List.of());

        List<AttemptResponse> response = getUserAttemptsService.getUserAttempts();

        assertEquals(0, response.size());

        verify(attemptsRepository).getUserAttempts(USER_ID);
    }

    private static AttemptEntityResponse firstAttemptEntity() {
        return AttemptEntityResponse.builder()
                .id(FIRST_ATTEMPT_ID)
                .taskId(FIRST_TASK_ID)
                .startedAt(FIRST_STARTED_AT)
                .submittedAt(FIRST_SUBMITTED_AT)
                .status(SUBMITTED)
                .score(FIRST_SCORE)
                .maxScoreSnapshot(MAX_SCORE_SNAPSHOT)
                .isCorrect(Boolean.TRUE)
                .build();
    }

    private static AttemptEntityResponse secondAttemptEntity() {
        return AttemptEntityResponse.builder()
                .id(SECOND_ATTEMPT_ID)
                .taskId(SECOND_TASK_ID)
                .startedAt(SECOND_STARTED_AT)
                .submittedAt(SECOND_SUBMITTED_AT)
                .status(CHECKED)
                .score(SECOND_SCORE)
                .maxScoreSnapshot(MAX_SCORE_SNAPSHOT)
                .isCorrect(Boolean.FALSE)
                .build();
    }

}
