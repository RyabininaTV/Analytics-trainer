package com.example.ratings_and_achievements.service;

import com.example.ratings_and_achievements.dto.responses.LeaderboardItemResponse;
import com.example.ratings_and_achievements.entity.responses.FindLeaderboardUsersResponseEntity;
import com.example.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetLeaderboardService {

    private static final int FIRST_POSITION = 1;
    private static final BigDecimal PERCENT_MULTIPLIER = BigDecimal.valueOf(100);
    private static final int COMPLETION_PERCENT_SCALE = 2;

    UserRepository userRepository;

    public List<LeaderboardItemResponse> getLeaderboard() {
        List<FindLeaderboardUsersResponseEntity> leaderboardUsers =
                userRepository.findLeaderboardUsers();

        return toResponseItems(leaderboardUsers);
    }

    @Nonnull
    private static List<LeaderboardItemResponse> toResponseItems(
            @Nonnull List<FindLeaderboardUsersResponseEntity> leaderboardUsers
    ) {
        List<LeaderboardItemResponse> responseItems = new ArrayList<>(leaderboardUsers.size());

        for (int i = 0; i < leaderboardUsers.size(); i++) {
            FindLeaderboardUsersResponseEntity user = leaderboardUsers.get(i);

            responseItems.add(LeaderboardItemResponse.builder()
                    .position(i + FIRST_POSITION)
                    .userId(user.userId())
                    .username(user.username())
                    .completedTasksCount(user.completedTasksCount())
                    .totalTasksCount(user.totalTasksCount())
                    .totalScore(user.totalScore())
                    .completionPercent(calculateCompletionPercent(user))
                    .lastActivityAt(user.lastActivityAt())
                    .build()
            );
        }

        return responseItems;
    }

    @Nonnull
    private static BigDecimal calculateCompletionPercent(@Nonnull FindLeaderboardUsersResponseEntity user) {
        if (user.totalTasksCount() == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(user.completedTasksCount())
                .multiply(PERCENT_MULTIPLIER)
                .divide(BigDecimal.valueOf(user.totalTasksCount()), COMPLETION_PERCENT_SCALE, HALF_UP);
    }

}
