package com.example.trainers.services;

import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.trainers.dto.responses.UserProgressByTrainerIdResponse;
import com.example.trainers.entities.requests.GetUserProgressByTrainerIdEntityRequest;
import com.example.trainers.entities.responses.UserProgressByTrainerIdEntityResponse;
import com.example.trainers.exceptions.UserProgressNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserProgressByTrainerIdServiceTest {

    private static final Long PROGRESS_ID = 100L;
    private static final Long USER_ID = 1L;
    private static final Long TRAINER_ID = 10L;

    private static final Integer COMPLETED_TASKS_COUNT = 7;
    private static final Integer TOTAL_TASKS_COUNT = 10;
    private static final Integer TOTAL_SCORE = 85;

    private static final BigDecimal COMPLETION_PERCENT = BigDecimal.valueOf(70);

    private static final LocalDateTime LAST_ACTIVITY_AT = LocalDateTime.of(2026, 4, 25, 12, 0);

    private static final String EMAIL = "test@mail.ru";
    private static final String USERNAME = "test_user";

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    UserProgressRepository userProgressRepository;

    @Nested
    class GetUserProgressByTrainerIdTest {

        @Test
        void userProgressExists_shouldReturnUserProgressByTrainerIdResponse() {
            when(currentUserContext.require())
                    .thenReturn(currentUser());

            when(userProgressRepository.getUserProgressByTrainerId(any(GetUserProgressByTrainerIdEntityRequest.class)))
                    .thenReturn(Optional.of(userProgress()));

            GetUserProgressByTrainerIdService service = new GetUserProgressByTrainerIdService(
                    currentUserContext,
                    userProgressRepository
            );

            UserProgressByTrainerIdResponse response = service.getUserProgressByTrainerId(TRAINER_ID);

            assertNotNull(response);
            assertEquals(PROGRESS_ID, response.id());
            assertEquals(USER_ID, response.userId());
            assertEquals(TRAINER_ID, response.trainerId());
            assertEquals(COMPLETED_TASKS_COUNT, response.completedTasksCount());
            assertEquals(TOTAL_TASKS_COUNT, response.totalTasksCount());
            assertEquals(TOTAL_SCORE, response.totalScore());
            assertEquals(COMPLETION_PERCENT, response.completionPercent());
            assertEquals(LAST_ACTIVITY_AT, response.lastActivityAt());

            ArgumentCaptor<GetUserProgressByTrainerIdEntityRequest> captor =
                    ArgumentCaptor.forClass(GetUserProgressByTrainerIdEntityRequest.class);

            verify(currentUserContext).require();
            verify(userProgressRepository).getUserProgressByTrainerId(captor.capture());
            verifyNoMoreInteractions(currentUserContext, userProgressRepository);

            GetUserProgressByTrainerIdEntityRequest request = captor.getValue();

            assertEquals(USER_ID, request.userId());
            assertEquals(TRAINER_ID, request.trainerId());
        }

        @Test
        void userProgressDoesNotExist_shouldThrowUserProgressNotFoundException() {
            when(currentUserContext.require())
                    .thenReturn(currentUser());

            when(userProgressRepository.getUserProgressByTrainerId(any(GetUserProgressByTrainerIdEntityRequest.class)))
                    .thenReturn(Optional.empty());

            GetUserProgressByTrainerIdService service = new GetUserProgressByTrainerIdService(
                    currentUserContext,
                    userProgressRepository
            );

            assertThrows(
                    UserProgressNotFoundException.class,
                    () -> service.getUserProgressByTrainerId(TRAINER_ID)
            );

            ArgumentCaptor<GetUserProgressByTrainerIdEntityRequest> captor =
                    ArgumentCaptor.forClass(GetUserProgressByTrainerIdEntityRequest.class);

            verify(currentUserContext).require();
            verify(userProgressRepository).getUserProgressByTrainerId(captor.capture());
            verifyNoMoreInteractions(currentUserContext, userProgressRepository);

            GetUserProgressByTrainerIdEntityRequest request = captor.getValue();

            assertEquals(USER_ID, request.userId());
            assertEquals(TRAINER_ID, request.trainerId());
        }

    }

    private static CurrentUser currentUser() {
        return CurrentUser.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username(USERNAME)
                .role(USER)
                .build();
    }

    private static UserProgressByTrainerIdEntityResponse userProgress() {
        return UserProgressByTrainerIdEntityResponse.builder()
                .id(PROGRESS_ID)
                .userId(USER_ID)
                .trainerId(TRAINER_ID)
                .completedTasksCount(COMPLETED_TASKS_COUNT)
                .totalTasksCount(TOTAL_TASKS_COUNT)
                .totalScore(TOTAL_SCORE)
                .completionPercent(COMPLETION_PERCENT)
                .lastActivityAt(LAST_ACTIVITY_AT)
                .build();
    }

}
