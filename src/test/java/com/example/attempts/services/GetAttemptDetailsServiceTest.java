package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptAnswerDetails;
import com.example.attempts.dto.responses.AttemptDetailsResponse;
import com.example.attempts.entities.requests.GetAttemptDetailsEntityRequest;
import com.example.attempts.entities.responses.AttemptAnswerWithTextEntityResponse;
import com.example.attempts.entities.responses.AttemptWithAnswersEntityResponse;
import com.example.attempts.exceptions.AttemptNotFoundException;
import com.example.jooq.generated.enums.AttemptStatusEnum;
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
import java.util.Optional;

import static com.example.jooq.generated.enums.AnswerTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttemptDetailsServiceTest {

    private static final Long USER_ID = 10L;
    private static final Long TASK_ID = 100L;
    private static final Long ATTEMPT_ID = 1000L;

    private static final Long FIRST_ANSWER_ID = 1L;
    private static final Long SECOND_ANSWER_ID = 2L;
    private static final Long THIRD_ANSWER_ID = 3L;

    private static final Long SELECTED_OPTION_ID = 11L;
    private static final Long SELECTED_ERROR_ITEM_ID = 22L;

    private static final String SELECTED_OPTION_TEXT = "Ответ 1";
    private static final String SELECTED_ERROR_ITEM_TEXT = "Ошибочный фрагмент";
    private static final String TEXT_ANSWER = "Свободный ответ пользователя";
    private static final String REVIEWER_COMMENT = "Комментарий ревьюера";

    private static final Integer SCORE = 8;
    private static final Integer MAX_SCORE_SNAPSHOT = 10;

    private static final LocalDateTime STARTED_AT = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final LocalDateTime SUBMITTED_AT = LocalDateTime.of(2026, 1, 1, 12, 10);
    private static final LocalDateTime REVIEWED_AT = LocalDateTime.of(2026, 1, 1, 12, 30);

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    CurrentUser currentUser;

    @Mock
    AttemptsRepository attemptsRepository;

    @InjectMocks
    GetAttemptDetailsService getAttemptDetailsService;

    @Test
    void getAttemptDetails_shouldReturnAttemptDetails() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getAttemptDetails(any(GetAttemptDetailsEntityRequest.class)))
                .thenReturn(Optional.of(attemptWithAnswersEntity()));

        AttemptDetailsResponse response = getAttemptDetailsService.getAttemptDetails(ATTEMPT_ID);

        assertEquals(ATTEMPT_ID, response.id());
        assertEquals(USER_ID, response.userId());
        assertEquals(TASK_ID, response.taskId());
        assertEquals(STARTED_AT, response.startedAt());
        assertEquals(SUBMITTED_AT, response.submittedAt());
        assertEquals(AttemptStatusEnum.CHECKED, response.status());
        assertEquals(SCORE, response.score());
        assertEquals(MAX_SCORE_SNAPSHOT, response.maxScoreSnapshot());
        assertEquals(Boolean.TRUE, response.isCorrect());
        assertEquals(Boolean.TRUE, response.autoChecked());
        assertEquals(Boolean.FALSE, response.needsManualReview());
        assertEquals(REVIEWER_COMMENT, response.reviewerComment());
        assertEquals(REVIEWED_AT, response.reviewedAt());

        assertNotNull(response.answers());
        assertEquals(3, response.answers().size());

        AttemptAnswerDetails firstAnswer = response.answers().getFirst();

        assertEquals(FIRST_ANSWER_ID, firstAnswer.id());
        assertEquals(TEST_OPTION, firstAnswer.answerType());
        assertEquals(SELECTED_OPTION_ID, firstAnswer.selectedOptionId());
        assertEquals(SELECTED_OPTION_TEXT, firstAnswer.selectedOptionText());
        assertNull(firstAnswer.selectedErrorItemId());
        assertNull(firstAnswer.selectedErrorItemText());
        assertNull(firstAnswer.textAnswer());

        AttemptAnswerDetails secondAnswer = response.answers().get(1);

        assertEquals(SECOND_ANSWER_ID, secondAnswer.id());
        assertEquals(ERROR_ITEM, secondAnswer.answerType());
        assertNull(secondAnswer.selectedOptionId());
        assertNull(secondAnswer.selectedOptionText());
        assertEquals(SELECTED_ERROR_ITEM_ID, secondAnswer.selectedErrorItemId());
        assertEquals(SELECTED_ERROR_ITEM_TEXT, secondAnswer.selectedErrorItemText());
        assertNull(secondAnswer.textAnswer());

        AttemptAnswerDetails thirdAnswer = response.answers().get(2);

        assertEquals(THIRD_ANSWER_ID, thirdAnswer.id());
        assertEquals(OPEN_TEXT, thirdAnswer.answerType());
        assertNull(thirdAnswer.selectedOptionId());
        assertNull(thirdAnswer.selectedOptionText());
        assertNull(thirdAnswer.selectedErrorItemId());
        assertNull(thirdAnswer.selectedErrorItemText());
        assertEquals(TEXT_ANSWER, thirdAnswer.textAnswer());

        ArgumentCaptor<GetAttemptDetailsEntityRequest> captor =
                ArgumentCaptor.forClass(GetAttemptDetailsEntityRequest.class);

        verify(attemptsRepository).getAttemptDetails(captor.capture());

        GetAttemptDetailsEntityRequest request = captor.getValue();

        assertEquals(ATTEMPT_ID, request.attemptId());
        assertEquals(USER_ID, request.userId());
    }

    @Test
    void getAttemptDetails_shouldThrowAttemptNotFoundException_whenAttemptDoesNotExist() {
        when(currentUserContext.require())
                .thenReturn(currentUser);

        when(currentUser.id())
                .thenReturn(USER_ID);

        when(attemptsRepository.getAttemptDetails(any(GetAttemptDetailsEntityRequest.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                AttemptNotFoundException.class,
                () -> getAttemptDetailsService.getAttemptDetails(ATTEMPT_ID)
        );

        verify(attemptsRepository).getAttemptDetails(any(GetAttemptDetailsEntityRequest.class));
    }

    private static AttemptWithAnswersEntityResponse attemptWithAnswersEntity() {
        return AttemptWithAnswersEntityResponse.builder()
                .id(ATTEMPT_ID)
                .userId(USER_ID)
                .taskId(TASK_ID)
                .startedAt(STARTED_AT)
                .submittedAt(SUBMITTED_AT)
                .status(AttemptStatusEnum.CHECKED)
                .score(SCORE)
                .maxScoreSnapshot(MAX_SCORE_SNAPSHOT)
                .isCorrect(Boolean.TRUE)
                .autoChecked(Boolean.TRUE)
                .needsManualReview(Boolean.FALSE)
                .reviewerComment(REVIEWER_COMMENT)
                .reviewedAt(REVIEWED_AT)
                .answers(List.of(
                        testOptionAnswerEntity(),
                        errorItemAnswerEntity(),
                        openTextAnswerEntity()
                ))
                .build();
    }

    private static AttemptAnswerWithTextEntityResponse testOptionAnswerEntity() {
        return AttemptAnswerWithTextEntityResponse.builder()
                .id(FIRST_ANSWER_ID)
                .answerType(TEST_OPTION)
                .selectedOptionId(SELECTED_OPTION_ID)
                .selectedOptionText(SELECTED_OPTION_TEXT)
                .selectedErrorItemId(null)
                .selectedErrorItemText(null)
                .textAnswer(null)
                .build();
    }

    private static AttemptAnswerWithTextEntityResponse errorItemAnswerEntity() {
        return AttemptAnswerWithTextEntityResponse.builder()
                .id(SECOND_ANSWER_ID)
                .answerType(ERROR_ITEM)
                .selectedOptionId(null)
                .selectedOptionText(null)
                .selectedErrorItemId(SELECTED_ERROR_ITEM_ID)
                .selectedErrorItemText(SELECTED_ERROR_ITEM_TEXT)
                .textAnswer(null)
                .build();
    }

    private static AttemptAnswerWithTextEntityResponse openTextAnswerEntity() {
        return AttemptAnswerWithTextEntityResponse.builder()
                .id(THIRD_ANSWER_ID)
                .answerType(OPEN_TEXT)
                .selectedOptionId(null)
                .selectedOptionText(null)
                .selectedErrorItemId(null)
                .selectedErrorItemText(null)
                .textAnswer(TEXT_ANSWER)
                .build();
    }

}
