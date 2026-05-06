package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptResponse;
import com.example.attempts.entities.requests.GetAttemptByTaskIdEntityRequest;
import com.example.attempts.exceptions.AttemptNotFoundException;
import com.example.repositories.AttemptsRepository;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetAttemptByTaskIdService {

    CurrentUserContext currentUserContext;
    AttemptsRepository attemptsRepository;

    public AttemptResponse getAttemptByTaskId(long taskId) {
        GetAttemptByTaskIdEntityRequest request = GetAttemptByTaskIdEntityRequest.builder()
                .userId(currentUserContext.require().id())
                .taskId(taskId)
                .build();

        var entity = attemptsRepository.getAttemptByTaskId(request)
                .orElseThrow(() -> new AttemptNotFoundException("No attempt found for task: " + taskId));

        return mapToDto(entity);
    }

    private AttemptResponse mapToDto(com.example.attempts.entities.responses.AttemptEntityResponse entity) {
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
