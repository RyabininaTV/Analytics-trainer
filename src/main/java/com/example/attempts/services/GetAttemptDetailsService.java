package com.example.attempts.services;

import com.example.attempts.dto.responses.AttemptAnswerDetails;
import com.example.attempts.dto.responses.AttemptDetailsResponse;
import com.example.attempts.entities.requests.GetAttemptDetailsEntityRequest;
import com.example.attempts.entities.responses.AttemptAnswerEntityResponse;
import com.example.attempts.entities.responses.AttemptWithAnswersEntityResponse;
import com.example.repositories.AttemptsRepository;
import com.example.repositories.TaskOptionsRepository;
import com.example.repositories.TaskErrorItemsRepository;
import com.example.security.current_user_context.CurrentUserContext;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetAttemptDetailsService {

    CurrentUserContext currentUserContext;
    AttemptsRepository attemptsRepository;
    TaskOptionsRepository taskOptionsRepository;
    TaskErrorItemsRepository taskErrorItemsRepository;

    public AttemptDetailsResponse getAttemptDetails(long attemptId) {
        GetAttemptDetailsEntityRequest request = GetAttemptDetailsEntityRequest.builder()
                .attemptId(attemptId)
                .userId(currentUserContext.require().id())
                .build();

        AttemptWithAnswersEntityResponse entity = attemptsRepository.getAttemptDetails(request);

        return AttemptDetailsResponse.builder()
                .id(entity.id())
                .userId(entity.userId())
                .taskId(entity.taskId())
                .startedAt(entity.startedAt())
                .submittedAt(entity.submittedAt())
                .status(entity.status())
                .score(entity.score())
                .maxScoreSnapshot(entity.maxScoreSnapshot())
                .isCorrect(entity.isCorrect())
                .autoChecked(entity.autoChecked())
                .needsManualReview(entity.needsManualReview())
                .reviewerComment(entity.reviewerComment())
                .reviewedAt(entity.reviewedAt())
                .answers(mapAnswers(entity.answers()))
                .build();
    }

    private List<AttemptAnswerDetails> mapAnswers(List<AttemptAnswerEntityResponse> answers) {
        return answers.stream()
                .map(answer -> {
                    var builder = AttemptAnswerDetails.builder()
                            .id(answer.id())
                            .answerType(answer.answerType())
                            .selectedOptionId(answer.selectedOptionId())
                            .selectedErrorItemId(answer.selectedErrorItemId())
                            .textAnswer(answer.textAnswer());

                    // Add text for selected option if exists
                    if (answer.selectedOptionId() != null) {
                        taskOptionsRepository.findOptionTextById(answer.selectedOptionId())
                                .ifPresent(builder::selectedOptionText);
                    }

                    // Add text for selected error item if exists
                    if (answer.selectedErrorItemId() != null) {
                        taskErrorItemsRepository.findFragmentTextById(answer.selectedErrorItemId())
                                .ifPresent(builder::selectedErrorItemText);
                    }

                    return builder.build();
                })
                .toList();
    }
}
