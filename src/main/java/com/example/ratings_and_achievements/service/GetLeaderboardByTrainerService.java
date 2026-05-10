package com.example.ratings_and_achievements.service;

import com.example.ratings_and_achievements.dto.responses.LeaderboardItemResponse;
import com.example.ratings_and_achievements.entity.responses.FindLeaderboardByTrainerIdResponseEntity;
import com.example.repositories.TrainersRepository;
import com.example.repositories.UserProgressRepository;
import com.example.trainers.exceptions.TrainerNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetLeaderboardByTrainerService {

    private static final int FIRST_POSITION = 1;

    TrainersRepository trainersRepository;
    UserProgressRepository userProgressRepository;

    public List<LeaderboardItemResponse> getLeaderboardByTrainer(Long trainerId) {
        if (!trainersRepository.existsActiveById(trainerId)) {
            throw new TrainerNotFoundException(trainerId);
        }

        List<FindLeaderboardByTrainerIdResponseEntity> leaderboardItems = userProgressRepository
                .findLeaderboardByTrainerId(trainerId);

        return toResponseItems(leaderboardItems);
    }

    @Nonnull
    private static List<LeaderboardItemResponse> toResponseItems(
            @Nonnull List<FindLeaderboardByTrainerIdResponseEntity> leaderboardItems
    ) {
        List<LeaderboardItemResponse> responseItems = new ArrayList<>(leaderboardItems.size());

        for (int i = 0; i < leaderboardItems.size(); i++) {
            FindLeaderboardByTrainerIdResponseEntity item = leaderboardItems.get(i);

            responseItems.add(LeaderboardItemResponse.builder()
                    .position(i + FIRST_POSITION)
                    .userId(item.userId())
                    .username(item.username())
                    .completedTasksCount(item.completedTasksCount())
                    .totalTasksCount(item.totalTasksCount())
                    .totalScore(item.totalScore())
                    .completionPercent(item.completionPercent())
                    .lastActivityAt(item.lastActivityAt())
                    .build()
            );
        }

        return responseItems;
    }

}
