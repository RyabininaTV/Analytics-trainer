package com.example.repositories;

import com.example.attempts.entities.responses.AttemptResultEntityResponse;
import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import com.example.attempts.entities.requests.GetAttemptByTaskIdEntityRequest;
import com.example.attempts.entities.requests.GetAttemptDetailsEntityRequest;
import com.example.attempts.entities.requests.SubmitAttemptEntityRequest;
import com.example.attempts.entities.responses.AttemptEntityResponse;
import com.example.attempts.entities.responses.AttemptWithAnswersEntityResponse;
import com.example.attempts.entities.responses.AttemptAnswerEntityResponse;
import com.example.attempts.exceptions.AttemptNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.jooq.generated.tables.Attempts.ATTEMPTS;
import static com.example.jooq.generated.tables.AttemptAnswers.ATTEMPT_ANSWERS;
import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AttemptsRepository {

    DSLContext dsl;

    public void deleteUserAttemptsByTrainerId(@Nonnull DeleteUserAttemptsByTrainerIdEntityRequest request) {
        dsl.deleteFrom(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(request.userId()))
                .and(ATTEMPTS.TASK_ID.in(
                        dsl.select(TASKS.ID)
                                .from(TASKS)
                                .where(TASKS.TRAINER_ID.eq(request.trainerId()))
                ))
                .execute();
    }

    public List<AttemptEntityResponse> getUserAttempts(@Nonnull Long userId) {
        return dsl.selectFrom(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(userId))
                .orderBy(ATTEMPTS.STARTED_AT.desc())
                .fetch(record -> AttemptEntityResponse.builder()
                        .id(record.getId())
                        .userId(record.getUserId())
                        .taskId(record.getTaskId())
                        .startedAt(record.getStartedAt())
                        .submittedAt(record.getSubmittedAt())
                        .status(record.getStatus())
                        .score(record.getScore())
                        .maxScoreSnapshot(record.getMaxScoreSnapshot())
                        .isCorrect(record.getIsCorrect())
                        .build()
                );
    }

    public Optional<AttemptEntityResponse> getAttemptByTaskId(@Nonnull GetAttemptByTaskIdEntityRequest request) {
        return dsl.selectFrom(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(request.userId()))
                .and(ATTEMPTS.TASK_ID.eq(request.taskId()))
                .orderBy(ATTEMPTS.STARTED_AT.desc())
                .limit(1)
                .fetchOptional(record -> AttemptEntityResponse.builder()
                        .id(record.getId())
                        .userId(record.getUserId())
                        .taskId(record.getTaskId())
                        .startedAt(record.getStartedAt())
                        .submittedAt(record.getSubmittedAt())
                        .status(record.getStatus())
                        .score(record.getScore())
                        .maxScoreSnapshot(record.getMaxScoreSnapshot())
                        .isCorrect(record.getIsCorrect())
                        .build()
                );
    }

    public AttemptWithAnswersEntityResponse getAttemptDetails(@Nonnull GetAttemptDetailsEntityRequest request) {
        // Получаем attempt
        var attemptRecord = dsl.selectFrom(ATTEMPTS)
                .where(ATTEMPTS.ID.eq(request.attemptId()))
                .and(ATTEMPTS.USER_ID.eq(request.userId()))
                .fetchOptional()
                .orElseThrow(() -> new AttemptNotFoundException(request.attemptId()));

        // Затем получаем все ответы по этому attempt
        List<AttemptAnswerEntityResponse> answers = dsl.selectFrom(ATTEMPT_ANSWERS)
                .where(ATTEMPT_ANSWERS.ATTEMPT_ID.eq(request.attemptId()))
                .fetch(record -> AttemptAnswerEntityResponse.builder()
                        .id(record.getId())
                        .attemptId(record.getAttemptId())
                        .answerType(record.getAnswerType())
                        .selectedOptionId(record.getSelectedOptionId())
                        .selectedErrorItemId(record.getSelectedErrorItemId())
                        .textAnswer(record.getTextAnswer())
                        .build()
                );

        return AttemptWithAnswersEntityResponse.builder()
                .id(attemptRecord.getId())
                .userId(attemptRecord.getUserId())
                .taskId(attemptRecord.getTaskId())
                .startedAt(attemptRecord.getStartedAt())
                .submittedAt(attemptRecord.getSubmittedAt())
                .status(attemptRecord.getStatus())
                .score(attemptRecord.getScore())
                .maxScoreSnapshot(attemptRecord.getMaxScoreSnapshot())
                .isCorrect(attemptRecord.getIsCorrect())
                .autoChecked(attemptRecord.getAutoChecked())
                .needsManualReview(attemptRecord.getNeedsManualReview())
                .reviewerComment(attemptRecord.getReviewerComment())
                .reviewedAt(attemptRecord.getReviewedAt())
                .answers(answers)
                .build();
    }

    /**
     * Создает новую попытку для TEST или ERROR_FIND типа задания (с автоматической проверкой)
     */
    @Transactional
    public AttemptResultEntityResponse createAutoCheckedAttempt(SubmitAttemptEntityRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // Создаем запись в attempts
        var attempt = dsl.insertInto(ATTEMPTS)
                .set(ATTEMPTS.USER_ID, request.userId())
                .set(ATTEMPTS.TASK_ID, request.taskId())
                .set(ATTEMPTS.STARTED_AT, now)
                .set(ATTEMPTS.SUBMITTED_AT, now)
                .set(ATTEMPTS.STATUS, request.status())
                .set(ATTEMPTS.SCORE, request.score())
                .set(ATTEMPTS.MAX_SCORE_SNAPSHOT, request.maxScore())
                .set(ATTEMPTS.IS_CORRECT, request.isCorrect())
                .set(ATTEMPTS.AUTO_CHECKED, true)
                .set(ATTEMPTS.REVIEWED_AT, now)
                .set(ATTEMPTS.NEEDS_MANUAL_REVIEW, false)
                .returning(ATTEMPTS.ID)
                .fetchOne();

        if (attempt == null) {
            throw new RuntimeException("Failed to create attempt");
        }

        // Создаем запись в attempt_answers
        dsl.insertInto(ATTEMPT_ANSWERS)
                .set(ATTEMPT_ANSWERS.ATTEMPT_ID, attempt.getId())
                .set(ATTEMPT_ANSWERS.ANSWER_TYPE, request.answerType())
                .set(ATTEMPT_ANSWERS.SELECTED_OPTION_ID, request.selectedOptionId())
                .set(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID, request.selectedErrorItemId())
                .set(ATTEMPT_ANSWERS.TEXT_ANSWER, request.textAnswer())
                .execute();

        return AttemptResultEntityResponse.builder()
                .attemptId(attempt.getId())
                .status(request.status())
                .score(request.score())
                .build();
    }

    /**
     * Создает новую попытку для OPEN типа задания (требует ручной проверки)
     */
    @Transactional
    public AttemptResultEntityResponse createOpenAttempt(SubmitAttemptEntityRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // Создаем запись в attempts
        var attempt = dsl.insertInto(ATTEMPTS)
                .set(ATTEMPTS.USER_ID, request.userId())
                .set(ATTEMPTS.TASK_ID, request.taskId())
                .set(ATTEMPTS.STARTED_AT, now)
                .set(ATTEMPTS.SUBMITTED_AT, now)
                .set(ATTEMPTS.STATUS, request.status())
                .set(ATTEMPTS.SCORE, (Integer) null)
                .set(ATTEMPTS.MAX_SCORE_SNAPSHOT, request.maxScore())
                .set(ATTEMPTS.IS_CORRECT, (Boolean) null)
                .set(ATTEMPTS.AUTO_CHECKED, false)
                .set(ATTEMPTS.REVIEWED_AT, (LocalDateTime) null)
                .set(ATTEMPTS.NEEDS_MANUAL_REVIEW, true)
                .returning(ATTEMPTS.ID)
                .fetchOne();

        if (attempt == null) {
            throw new RuntimeException("Failed to create attempt");
        }

        // Создаем запись в attempt_answers
        dsl.insertInto(ATTEMPT_ANSWERS)
                .set(ATTEMPT_ANSWERS.ATTEMPT_ID, attempt.getId())
                .set(ATTEMPT_ANSWERS.ANSWER_TYPE, request.answerType())
                .set(ATTEMPT_ANSWERS.SELECTED_OPTION_ID, (Long) null)
                .set(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID, (Long) null)
                .set(ATTEMPT_ANSWERS.TEXT_ANSWER, request.textAnswer())
                .execute();

        return AttemptResultEntityResponse.builder()
                .attemptId(attempt.getId())
                .status(request.status())
                .score(null)
                .build();
    }

    /**
     * Проверяет, есть ли у пользователя хотя бы одна попытка по данному заданию (любого статуса)
     */
    public boolean hasUserAttemptForTask(@Nonnull Long userId, @Nonnull Long taskId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(ATTEMPTS)
                        .where(ATTEMPTS.USER_ID.eq(userId))
                        .and(ATTEMPTS.TASK_ID.eq(taskId))
        );
    }

    /**
     * Получает сумму баллов пользователя по всем заданиям тренажера
     */
    public Integer getTotalScoreByUserAndTrainer(@Nonnull Long userId, @Nonnull Long trainerId) {
        return dsl.select(org.jooq.impl.DSL.sum(ATTEMPTS.SCORE))
                .from(ATTEMPTS)
                .join(TASKS).on(ATTEMPTS.TASK_ID.eq(TASKS.ID))
                .where(ATTEMPTS.USER_ID.eq(userId))
                .and(TASKS.TRAINER_ID.eq(trainerId))
                .fetchOne(0, Integer.class);
    }
}
