package com.example.progress_and_profile.services;

import com.example.progress_and_profile.dto.responses.ProgressResponse;
import com.example.progress_and_profile.entity.responses.FindTotalUserProgressResponseEntity;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUserProgressService {

    private static final BigDecimal PERCENT_MULTIPLIER = BigDecimal.valueOf(100);
    private static final int COMPLETION_PERCENT_SCALE = 2;

    CurrentUserContext currentUserContext;

    UserProgressRepository userProgressRepository;

    public ProgressResponse getUserProgress() {
        CurrentUser currentUser = currentUserContext.require();

        FindTotalUserProgressResponseEntity progress = userProgressRepository.findTotalByUserId(currentUser.id());

        BigDecimal completionPercent = calculateCompletionPercent(progress);

        return ProgressResponse.builder()
                .completedTasksCount(progress.completedTasksCount())
                .totalTasksCount(progress.totalTasksCount())
                .totalScore(progress.totalScore())
                .completionPercent(completionPercent)
                .lastActivityAt(progress.lastActivityAt())
                .build();
    }

    private static BigDecimal calculateCompletionPercent(@Nonnull FindTotalUserProgressResponseEntity progress) {
        int completedTasksCount = progress.completedTasksCount();
        int totalTasksCount = progress.totalTasksCount();

        if (totalTasksCount == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(completedTasksCount)
                .multiply(PERCENT_MULTIPLIER)
                .divide(BigDecimal.valueOf(totalTasksCount), COMPLETION_PERCENT_SCALE, HALF_UP);
    }

}
