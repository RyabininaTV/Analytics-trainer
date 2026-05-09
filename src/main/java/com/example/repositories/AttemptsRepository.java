package com.example.repositories;

import com.example.progress_and_profile.dto.responses.FindCheckedAttemptsByUserIdResponseEntity;
import com.example.attempts.entities.responses.*;
import com.example.trainers.entities.requests.DeleteUserAttemptsByTrainerIdEntityRequest;
import com.example.attempts.entities.requests.GetAttemptByTaskIdEntityRequest;
import com.example.attempts.entities.requests.GetAttemptDetailsEntityRequest;
import com.example.attempts.entities.requests.SubmitAttemptEntityRequest;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.jooq.generated.enums.AttemptStatusEnum.CHECKED;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.jooq.generated.tables.Attempts.ATTEMPTS;
import static com.example.jooq.generated.tables.AttemptAnswers.ATTEMPT_ANSWERS;
import static com.example.jooq.generated.tables.TaskOptions.TASK_OPTIONS;
import static com.example.jooq.generated.tables.TaskErrorItems.TASK_ERROR_ITEMS;
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

    public List<FindCheckedAttemptsByUserIdResponseEntity> findCheckedAttemptsByUserId(@Nonnull Long userId) {
        Field<LocalDateTime> changedAt = DSL.coalesce(ATTEMPTS.REVIEWED_AT, ATTEMPTS.SUBMITTED_AT);

        return dsl.select(
                        ATTEMPTS.TASK_ID,
                        changedAt,
                        ATTEMPTS.SCORE
                )
                .from(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(userId))
                .and(ATTEMPTS.STATUS.eq(CHECKED))
                .and(ATTEMPTS.SCORE.isNotNull())
                .and(changedAt.isNotNull())
                .orderBy(changedAt.asc(), ATTEMPTS.ID.asc())
                .fetch(record -> FindCheckedAttemptsByUserIdResponseEntity.builder()
                        .taskId(record.get(ATTEMPTS.TASK_ID))
                        .changedAt(record.get(changedAt))
                        .score(record.get(ATTEMPTS.SCORE))
                        .build()
                );
    }

    public List<AttemptEntityResponse> getUserAttempts(Long userId) {
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

    public List<AttemptEntityResponse> getAttemptsByTaskId(@Nonnull GetAttemptByTaskIdEntityRequest request) {
        return dsl.selectFrom(ATTEMPTS)
                .where(ATTEMPTS.USER_ID.eq(request.userId()))
                .and(ATTEMPTS.TASK_ID.eq(request.taskId()))
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

    public Optional<AttemptWithAnswersEntityResponse> getAttemptDetails(@Nonnull GetAttemptDetailsEntityRequest request) {
        // Получаем attempt с joined данными из task_options и task_error_items
        var attemptRecord = dsl.select(
                        ATTEMPTS.ID,
                        ATTEMPTS.USER_ID,
                        ATTEMPTS.TASK_ID,
                        ATTEMPTS.STARTED_AT,
                        ATTEMPTS.SUBMITTED_AT,
                        ATTEMPTS.STATUS,
                        ATTEMPTS.SCORE,
                        ATTEMPTS.MAX_SCORE_SNAPSHOT,
                        ATTEMPTS.IS_CORRECT,
                        ATTEMPTS.AUTO_CHECKED,
                        ATTEMPTS.NEEDS_MANUAL_REVIEW,
                        ATTEMPTS.REVIEWER_COMMENT,
                        ATTEMPTS.REVIEWED_AT
                )
                .from(ATTEMPTS)
                .where(ATTEMPTS.ID.eq(request.attemptId()))
                .and(ATTEMPTS.USER_ID.eq(request.userId()))
                .fetchOptional();

        if (attemptRecord.isEmpty()) {
            return Optional.empty();
        }

        var record = attemptRecord.get();

        // Получаем все ответы с joined текстами из соответствующих таблиц
        List<AttemptAnswerWithTextEntityResponse> answers = dsl.select(
                        ATTEMPT_ANSWERS.ID,
                        ATTEMPT_ANSWERS.ANSWER_TYPE,
                        ATTEMPT_ANSWERS.SELECTED_OPTION_ID,
                        ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID,
                        ATTEMPT_ANSWERS.TEXT_ANSWER,
                        TASK_OPTIONS.OPTION_TEXT,
                        TASK_ERROR_ITEMS.FRAGMENT_TEXT
                )
                .from(ATTEMPT_ANSWERS)
                .leftJoin(TASK_OPTIONS)
                .on(ATTEMPT_ANSWERS.SELECTED_OPTION_ID.eq(TASK_OPTIONS.ID))
                .leftJoin(TASK_ERROR_ITEMS)
                .on(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID.eq(TASK_ERROR_ITEMS.ID))
                .where(ATTEMPT_ANSWERS.ATTEMPT_ID.eq(request.attemptId()))
                .fetch()
                .map(fetchRecord -> AttemptAnswerWithTextEntityResponse.builder()
                        .id(fetchRecord.get(ATTEMPT_ANSWERS.ID))
                        .answerType(fetchRecord.get(ATTEMPT_ANSWERS.ANSWER_TYPE))
                        .selectedOptionId(fetchRecord.get(ATTEMPT_ANSWERS.SELECTED_OPTION_ID))
                        .selectedOptionText(fetchRecord.get(TASK_OPTIONS.OPTION_TEXT))
                        .selectedErrorItemId(fetchRecord.get(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID))
                        .selectedErrorItemText(fetchRecord.get(TASK_ERROR_ITEMS.FRAGMENT_TEXT))
                        .textAnswer(fetchRecord.get(ATTEMPT_ANSWERS.TEXT_ANSWER))
                        .build()
                );

        return Optional.of(AttemptWithAnswersEntityResponse.builder()
                .id(record.get(ATTEMPTS.ID))
                .userId(record.get(ATTEMPTS.USER_ID))
                .taskId(record.get(ATTEMPTS.TASK_ID))
                .startedAt(record.get(ATTEMPTS.STARTED_AT))
                .submittedAt(record.get(ATTEMPTS.SUBMITTED_AT))
                .status(record.get(ATTEMPTS.STATUS))
                .score(record.get(ATTEMPTS.SCORE))
                .maxScoreSnapshot(record.get(ATTEMPTS.MAX_SCORE_SNAPSHOT))
                .isCorrect(record.get(ATTEMPTS.IS_CORRECT))
                .autoChecked(record.get(ATTEMPTS.AUTO_CHECKED))
                .needsManualReview(record.get(ATTEMPTS.NEEDS_MANUAL_REVIEW))
                .reviewerComment(record.get(ATTEMPTS.REVIEWER_COMMENT))
                .reviewedAt(record.get(ATTEMPTS.REVIEWED_AT))
                .answers(answers)
                .build());
    }

    /**
     * Создает новую попытку для TEST или ERROR_FIND типа задания (с автоматической проверкой)
     */
    public Optional<AttemptResultEntityResponse> createAutoCheckedAttempt(SubmitAttemptEntityRequest request) {
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
            return Optional.empty();
        }

        // Создаем запись в attempt_answers
        dsl.insertInto(ATTEMPT_ANSWERS)
                .set(ATTEMPT_ANSWERS.ATTEMPT_ID, attempt.getId())
                .set(ATTEMPT_ANSWERS.ANSWER_TYPE, request.answerType())
                .set(ATTEMPT_ANSWERS.SELECTED_OPTION_ID, request.selectedOptionId())
                .set(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID, request.selectedErrorItemId())
                .set(ATTEMPT_ANSWERS.TEXT_ANSWER, request.textAnswer())
                .execute();

        return Optional.of(AttemptResultEntityResponse.builder()
                .attemptId(attempt.getId())
                .status(request.status())
                .score(request.score())
                .build());
    }

    /**
     * Создает новую попытку для OPEN типа задания (требует ручной проверки)
     */
     public Optional<AttemptResultEntityResponse> createOpenAttempt(SubmitAttemptEntityRequest request) {
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
            return Optional.empty();
        }

        // Создаем запись в attempt_answers
        dsl.insertInto(ATTEMPT_ANSWERS)
                .set(ATTEMPT_ANSWERS.ATTEMPT_ID, attempt.getId())
                .set(ATTEMPT_ANSWERS.ANSWER_TYPE, request.answerType())
                .set(ATTEMPT_ANSWERS.SELECTED_OPTION_ID, (Long) null)
                .set(ATTEMPT_ANSWERS.SELECTED_ERROR_ITEM_ID, (Long) null)
                .set(ATTEMPT_ANSWERS.TEXT_ANSWER, request.textAnswer())
                .execute();

        return Optional.of(AttemptResultEntityResponse.builder()
                .attemptId(attempt.getId())
                .status(request.status())
                .score(null)
                .build());
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
    public Optional<Integer> getTotalScoreByUserAndTrainer(@Nonnull Long userId, @Nonnull Long trainerId) {
        Integer totalScore = dsl.select(org.jooq.impl.DSL.sum(ATTEMPTS.SCORE))
                .from(ATTEMPTS)
                .join(TASKS).on(ATTEMPTS.TASK_ID.eq(TASKS.ID))
                .where(ATTEMPTS.USER_ID.eq(userId))
                .and(TASKS.TRAINER_ID.eq(trainerId))
                .fetchOne(0, Integer.class);

        return Optional.ofNullable(totalScore);
    }
}
