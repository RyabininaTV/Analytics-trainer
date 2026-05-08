package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.FindCheckedAttemptsByUserIdResponseEntity;
import com.example.progress_and_profile.dto.responses.ProgressHistoryItemResponse;
import com.example.repositories.AttemptsRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetHistoryOfUserProgressChangesService {

    private static final int INITIAL_SCORE = 0;

    CurrentUserContext currentUserContext;

    AttemptsRepository attemptsRepository;

    public List<ProgressHistoryItemResponse> getHistoryOfUserProgressChanges() {
        CurrentUser currentUser = currentUserContext.require();

        List<FindCheckedAttemptsByUserIdResponseEntity> attempts = attemptsRepository
                .findCheckedAttemptsByUserId(currentUser.id());

        return buildHistoryItems(attempts);
    }

    @Nonnull
    private static List<ProgressHistoryItemResponse> buildHistoryItems(
            @Nonnull List<FindCheckedAttemptsByUserIdResponseEntity> attempts
    ) {
        Map<Long, Integer> bestScoresByTaskId = new HashMap<>(attempts.size());
        List<ProgressHistoryItemResponse> historyItems = new ArrayList<>(attempts.size());

        int totalScore = INITIAL_SCORE;

        for (FindCheckedAttemptsByUserIdResponseEntity attempt : attempts) {
            int previousBestScore = bestScoresByTaskId.getOrDefault(attempt.taskId(), INITIAL_SCORE);

            if (attempt.score() <= previousBestScore) {
                continue;
            }

            int scoreDelta = attempt.score() - previousBestScore;

            bestScoresByTaskId.put(attempt.taskId(), attempt.score());
            totalScore += scoreDelta;

            historyItems.add(ProgressHistoryItemResponse.builder()
                    .changedAt(attempt.changedAt())
                    .totalScore(totalScore)
                    .build()
            );
        }

        return historyItems;
    }

}
