package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptResponse;
import com.example.attempts.entities.requests.GetAttemptByTaskIdEntityRequest;
import com.example.attempts.entities.responses.AttemptEntityResponse;
import com.example.attempts.exceptions.TaskAttemptNotFoundException;
import com.example.repositories.AttemptsRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.jooq.generated.enums.AttemptStatusEnum.CHECKED;
import static com.example.jooq.generated.enums.AttemptStatusEnum.SUBMITTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttemptByTaskIdServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long TASK_ID = 100L;

    private static final Long FIRST_ATTEMPT_ID = 1L;
    private static final Long SECOND_ATTEMPT_ID = 2L;

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
    GetAttemptByTaskIdService getAttemptByTaskIdService;

    @Test
    void getAttemptsByTaskId_shouldReturnAttempts() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getAttemptsByTaskId(any(GetAttemptByTaskIdEntityRequest.class)))
                .thenReturn(List.of(
                        firstAttemptEntity(),
                        secondAttemptEntity()
                ));

        List<AttemptResponse> response = getAttemptByTaskIdService.getAttemptsByTaskId(TASK_ID);

        assertEquals(2, response.size());

        AttemptResponse firstAttempt = response.getFirst();

        assertEquals(FIRST_ATTEMPT_ID, firstAttempt.id());
        assertEquals(TASK_ID, firstAttempt.taskId());
        assertEquals(FIRST_STARTED_AT, firstAttempt.startedAt());
        assertEquals(FIRST_SUBMITTED_AT, firstAttempt.submittedAt());
        assertEquals(SUBMITTED, firstAttempt.status());
        assertEquals(FIRST_SCORE, firstAttempt.score());
        assertEquals(MAX_SCORE_SNAPSHOT, firstAttempt.maxScoreSnapshot());
        assertEquals(Boolean.TRUE, firstAttempt.isCorrect());

        ArgumentCaptor<GetAttemptByTaskIdEntityRequest> captor =
                ArgumentCaptor.forClass(GetAttemptByTaskIdEntityRequest.class);

        verify(attemptsRepository).getAttemptsByTaskId(captor.capture());

        GetAttemptByTaskIdEntityRequest entityRequest = captor.getValue();

        assertEquals(USER_ID, entityRequest.userId());
        assertEquals(TASK_ID, entityRequest.taskId());
    }

    @Test
    void getAttemptsByTaskId_shouldThrowTaskAttemptNotFoundException_whenAttemptsNotFound() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getAttemptsByTaskId(any(GetAttemptByTaskIdEntityRequest.class)))
                .thenReturn(List.of());

        assertThrows(
                TaskAttemptNotFoundException.class,
                () -> getAttemptByTaskIdService.getAttemptsByTaskId(TASK_ID)
        );

        verify(attemptsRepository).getAttemptsByTaskId(any(GetAttemptByTaskIdEntityRequest.class));
    }

    private static AttemptEntityResponse firstAttemptEntity() {
        return AttemptEntityResponse.builder()
                .id(FIRST_ATTEMPT_ID)
                .taskId(TASK_ID)
                .startedAt(FIRST_STARTED_AT)
                .submittedAt(FIRST_SUBMITTED_AT)
                .status(SUBMITTED)
                .score(FIRST_SCORE)
                .maxScoreSnapshot(MAX_SCORE_SNAPSHOT)
                .isCorrect(true)
                .build();
    }

    private static AttemptEntityResponse secondAttemptEntity() {
        return AttemptEntityResponse.builder()
                .id(SECOND_ATTEMPT_ID)
                .taskId(TASK_ID)
                .startedAt(SECOND_STARTED_AT)
                .submittedAt(SECOND_SUBMITTED_AT)
                .status(CHECKED)
                .score(SECOND_SCORE)
                .maxScoreSnapshot(MAX_SCORE_SNAPSHOT)
                .isCorrect(true)
                .build();
    }

}
