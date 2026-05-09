package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptResponse;
import com.example.attempts.entities.requests.GetAttemptByTaskIdEntityRequest;
import com.example.attempts.entities.responses.AttemptEntityResponse;
import com.example.attempts.exceptions.TaskAttemptNotFoundException;
import com.example.repositories.AttemptsRepository;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetAttemptByTaskIdService {

    CurrentUserContext currentUserContext;
    AttemptsRepository attemptsRepository;

    public List<AttemptResponse> getAttemptsByTaskId(long taskId) {
        GetAttemptByTaskIdEntityRequest request = GetAttemptByTaskIdEntityRequest.builder()
                .userId(currentUserContext.require().id())
                .taskId(taskId)
                .build();

        List<AttemptEntityResponse> entities = attemptsRepository.getAttemptsByTaskId(request);

        if (entities.isEmpty()) {
            throw new TaskAttemptNotFoundException(taskId);
        }

        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }

    private AttemptResponse mapToDto(@Nonnull AttemptEntityResponse entity) {
        return AttemptResponse.builder()
                .id(entity.id())
                .taskId(entity.taskId())
                .startedAt(entity.startedAt())
                .submittedAt(entity.submittedAt())
                .status(entity.status())
                .score(entity.score())
                .maxScoreSnapshot(entity.maxScoreSnapshot())
                .isCorrect(entity.isCorrect())
                .build();
    }
}
