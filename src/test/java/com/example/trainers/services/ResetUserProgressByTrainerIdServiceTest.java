package com.example.trainers.services;

import com.example.repositories.AttemptsRepository;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import com.example.trainers.entities.requests.DeleteUserProgressByTrainerIdEntityRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetUserProgressByTrainerIdServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long TRAINER_ID = 10L;

    private static final String EMAIL = "test@mail.ru";
    private static final String USERNAME = "test_user";

    @Mock
    CurrentUserContext currentUserContext;

    @Mock
    UserProgressRepository userProgressRepository;

    @Mock
    AttemptsRepository attemptsRepository;

    @Nested
    class ResetUserProgressByTrainerIdTest {

        @Test
        void userIsAuthorized_shouldDeleteUserProgressAndAttemptsByTrainerId() {
            when(currentUserContext.require())
                    .thenReturn(currentUser());

            ResetUserProgressByTrainerIdService service = new ResetUserProgressByTrainerIdService(
                    currentUserContext,
                    userProgressRepository,
                    attemptsRepository
            );

            service.resetUserProgressByTrainerId(TRAINER_ID);

            ArgumentCaptor<DeleteUserProgressByTrainerIdEntityRequest> progressRequestCaptor =
                    ArgumentCaptor.forClass(DeleteUserProgressByTrainerIdEntityRequest.class);

            ArgumentCaptor<DeleteUserAttemptsByTrainerIdEntityRequest> attemptsRequestCaptor =
                    ArgumentCaptor.forClass(DeleteUserAttemptsByTrainerIdEntityRequest.class);

            InOrder inOrder = inOrder(currentUserContext, userProgressRepository, attemptsRepository);

            inOrder.verify(currentUserContext).require();
            inOrder.verify(userProgressRepository).deleteUserProgressByTrainerId(progressRequestCaptor.capture());
            inOrder.verify(attemptsRepository).deleteUserAttemptsByTrainerId(attemptsRequestCaptor.capture());

            verifyNoMoreInteractions(currentUserContext, userProgressRepository, attemptsRepository);

            DeleteUserProgressByTrainerIdEntityRequest progressRequest = progressRequestCaptor.getValue();

            assertEquals(USER_ID, progressRequest.userId());
            assertEquals(TRAINER_ID, progressRequest.trainerId());

            DeleteUserAttemptsByTrainerIdEntityRequest attemptsRequest = attemptsRequestCaptor.getValue();

            assertEquals(USER_ID, attemptsRequest.userId());
            assertEquals(TRAINER_ID, attemptsRequest.trainerId());
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

}
