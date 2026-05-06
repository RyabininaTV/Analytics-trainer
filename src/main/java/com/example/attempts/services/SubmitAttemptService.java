package com.example.attempts.services;

import com.example.attempts.dto.requests.SubmitAttemptRequest;
import com.example.attempts.dto.responses.SubmitAttemptResponse;
import com.example.attempts.entities.requests.SubmitAttemptEntityRequest;
import com.example.attempts.entities.requests.UpdateUserProgressEntityRequest;
import com.example.attempts.entities.responses.AttemptResultEntityResponse;
import com.example.attempts.exceptions.InvalidAnswerException;
import com.example.attempts.exceptions.TaskNotFoundException;
import com.example.repositories.AttemptsRepository;
import com.example.repositories.UserProgressRepository;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.jooq.generated.enums.AnswerTypeEnum;
import com.example.jooq.generated.enums.AttemptStatusEnum;
import com.example.jooq.generated.enums.TaskTypeEnum;
import com.example.jooq.generated.tables.records.TaskErrorItemsRecord;
import com.example.jooq.generated.tables.records.TaskOptionsRecord;
import com.example.jooq.generated.tables.records.TasksRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.jooq.generated.Tables.ATTEMPTS;
import static com.example.jooq.generated.tables.TaskErrorItems.TASK_ERROR_ITEMS;
import static com.example.jooq.generated.tables.TaskOptions.TASK_OPTIONS;
import static com.example.jooq.generated.tables.Tasks.TASKS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SubmitAttemptService {

    CurrentUserContext currentUserContext;
    DSLContext dsl;
    AttemptsRepository attemptsRepository;
    UserProgressRepository userProgressRepository;

    @Transactional
    public SubmitAttemptResponse submitAttempt(SubmitAttemptRequest request) {
        Long userId = resolveUserId(request);
        TasksRecord task = getTask(request.getTaskId());

        validateDuplicateSubmission(userId, task);

        SubmitAttemptEntityRequest entityRequest;
        TaskTypeEnum taskType = task.getTaskType();

        if (taskType == TaskTypeEnum.TEST) {
            Long optionId = parseAnswerId(request.getAnswer());
            TaskOptionsRecord option = getTaskOption(optionId, task.getId());
            entityRequest = buildTestRequest(userId, request, task, option);
        } else if (taskType == TaskTypeEnum.ERROR_FIND) {
            Long errorItemId = parseAnswerId(request.getAnswer());
            TaskErrorItemsRecord errorItem = getTaskErrorItem(errorItemId, task.getId());
            entityRequest = buildErrorFindRequest(userId, request, task, errorItem);
        } else {
            entityRequest = buildOpenRequest(userId, request, task);
        }

        AttemptResultEntityResponse result = processAttempt(entityRequest, task);

        // Обновляем прогресс, всегда получая актуальные данные из БД
        updateUserProgress(userId, task.getTrainerId(), taskType);

        Integer totalScore = Optional.ofNullable(
                attemptsRepository.getTotalScoreByUserAndTrainer(userId, task.getTrainerId())
        ).orElse(0);

        return buildResponse(request.getTaskId(), userId, result, totalScore);
    }

    private Long resolveUserId(SubmitAttemptRequest request) {
        return Optional.ofNullable(request.getUserId())
                .orElseGet(() -> currentUserContext.require().id());
    }

    private TasksRecord getTask(Long taskId) {
        return dsl.selectFrom(TASKS)
                .where(TASKS.ID.eq(taskId))
                .fetchOptional()
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private void validateDuplicateSubmission(Long userId, TasksRecord task) {
        boolean hasExistingAttempt = attemptsRepository.hasUserAttemptForTask(userId, task.getId());

        if (hasExistingAttempt) {
            throw new InvalidAnswerException("Task already completed correctly");
        }
    }

    private SubmitAttemptEntityRequest buildTestRequest(Long userId, SubmitAttemptRequest request,
                                                        TasksRecord task, TaskOptionsRecord option) {
        return SubmitAttemptEntityRequest.builder()
                .userId(userId)
                .taskId(task.getId())
                .maxScore(task.getMaxScore())
                .status(option.getIsCorrect() ? AttemptStatusEnum.CHECKED : AttemptStatusEnum.REJECTED)
                .score(option.getIsCorrect() ? task.getMaxScore() : null)
                .isCorrect(option.getIsCorrect())
                .answerType(AnswerTypeEnum.TEST_OPTION)
                .selectedOptionId(Long.parseLong(request.getAnswer()))
                .build();
    }

    private SubmitAttemptEntityRequest buildErrorFindRequest(Long userId, SubmitAttemptRequest request,
                                                             TasksRecord task, TaskErrorItemsRecord errorItem) {
        return SubmitAttemptEntityRequest.builder()
                .userId(userId)
                .taskId(task.getId())
                .maxScore(task.getMaxScore())
                .status(errorItem.getIsError() ? AttemptStatusEnum.CHECKED : AttemptStatusEnum.REJECTED)
                .score(errorItem.getIsError() ? task.getMaxScore() : null)
                .isCorrect(errorItem.getIsError())
                .answerType(AnswerTypeEnum.ERROR_ITEM)
                .selectedErrorItemId(Long.parseLong(request.getAnswer()))
                .build();
    }

    private SubmitAttemptEntityRequest buildOpenRequest(Long userId, SubmitAttemptRequest request, TasksRecord task) {
        String answerText = request.getAnswer();
        boolean isValid = !answerText.trim().isEmpty();

        return SubmitAttemptEntityRequest.builder()
                .userId(userId)
                .taskId(task.getId())
                .maxScore(task.getMaxScore())
                .status(isValid ? AttemptStatusEnum.SUBMITTED : AttemptStatusEnum.REJECTED)
                .answerType(AnswerTypeEnum.OPEN_TEXT)
                .textAnswer(answerText)
                .build();
    }

    private AttemptResultEntityResponse processAttempt(SubmitAttemptEntityRequest request, TasksRecord task) {
        return switch (task.getTaskType()) {
            case TEST, ERROR_FIND -> attemptsRepository.createAutoCheckedAttempt(request);
            case OPEN -> attemptsRepository.createOpenAttempt(request);
        };
    }

    private void updateUserProgress(Long userId, Long trainerId, TaskTypeEnum taskType) {
        // Всегда получаем актуальные данные из БД
        Integer totalScore = attemptsRepository.getTotalScoreByUserAndTrainer(userId, trainerId);
        if (totalScore == null) totalScore = 0;

        // Получаем актуальное количество выполненных заданий (только TEST и ERROR_FIND)
        Integer currentCompletedTasksCount = getCurrentCompletedTasksCount(userId, trainerId);

        // Получаем актуальное общее количество заданий в тренажере (может измениться)
        Integer totalTasksCount = getTotalTasksCount(trainerId);

        // Получаем актуальный максимально возможный балл (может измениться)
        Integer maxTotalScore = getMaxTotalScore(trainerId);

        // Увеличиваем completedTasksCount только для TEST и ERROR_FIND (если это задание еще не учтено)
        int newCompletedTasksCount = currentCompletedTasksCount;
        if (taskType == TaskTypeEnum.TEST || taskType == TaskTypeEnum.ERROR_FIND) {
            // Проверяем, не учтено ли уже это задание (на случай повторной отправки)
            boolean alreadyCounted = isTaskAlreadyCountedInProgress(taskType);
            if (!alreadyCounted) {
                newCompletedTasksCount = currentCompletedTasksCount + 1;
            }
        }

        BigDecimal completionPercent = calculateCompletionPercent(totalScore, maxTotalScore);

        userProgressRepository.updateProgress(
                UpdateUserProgressEntityRequest.builder()
                        .userId(userId)
                        .trainerId(trainerId)
                        .totalScore(totalScore)
                        .completedTasksCount(newCompletedTasksCount)
                        .totalTasksCount(totalTasksCount)
                        .completionPercent(completionPercent)
                        .build()
        );
    }

    /**
     * Проверяет, учтено ли уже это задание в прогрессе пользователя
     * (чтобы не увеличивать счетчик при повторных попытках)
     */
    private boolean isTaskAlreadyCountedInProgress(TaskTypeEnum taskType) {
        // Для OPEN заданий счетчик не увеличивается
        return taskType == TaskTypeEnum.OPEN;
    }

    /**
     * Получает количество уже выполненных заданий (только TEST и ERROR_FIND)
     * Считаем количество уникальных заданий, по которым есть хотя бы одна попытка
     */
    private Integer getCurrentCompletedTasksCount(Long userId, Long trainerId) {
        Integer count = dsl.selectCount()
                .from(ATTEMPTS)
                .join(TASKS).on(ATTEMPTS.TASK_ID.eq(TASKS.ID))
                .where(ATTEMPTS.USER_ID.eq(userId))
                .and(TASKS.TRAINER_ID.eq(trainerId))
                .and(TASKS.TASK_TYPE.in(TaskTypeEnum.TEST, TaskTypeEnum.ERROR_FIND))
                .fetchOne(0, Integer.class);

        return count != null ? count : 0;
    }

    /**
     * Получает актуальное общее количество заданий в тренажере (все типы)
     * Может измениться, если администратор добавил/удалил задания
     */
    private Integer getTotalTasksCount(Long trainerId) {
        Integer count = dsl.selectCount()
                .from(TASKS)
                .where(TASKS.TRAINER_ID.eq(trainerId))
                .fetchOne(0, Integer.class);

        return count != null ? count : 0;
    }

    /**
     * Получает актуальный максимально возможный балл за все задания тренажера
     * Может измениться, если администратор изменил баллы заданий
     */
    private Integer getMaxTotalScore(Long trainerId) {
        Integer maxScore = dsl.select(org.jooq.impl.DSL.sum(TASKS.MAX_SCORE))
                .from(TASKS)
                .where(TASKS.TRAINER_ID.eq(trainerId))
                .fetchOne(0, Integer.class);

        return maxScore != null ? maxScore : 0;
    }

    private BigDecimal calculateCompletionPercent(Integer totalScore, Integer maxTotalScore) {
        if (maxTotalScore == null || maxTotalScore == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(maxTotalScore), 2, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private SubmitAttemptResponse buildResponse(Long taskId, Long userId, AttemptResultEntityResponse result, Integer totalScore) {
        return SubmitAttemptResponse.builder()
                .taskId(taskId)
                .userId(userId)
                .attemptId(result.attemptId())
                .status(result.status())
                .score(result.score())
                .totalScore(totalScore)
                .build();
    }

    private Long parseAnswerId(String answer) {
        try {
            return Long.parseLong(answer);
        } catch (NumberFormatException e) {
            throw new InvalidAnswerException("Answer must be a valid ID");
        }
    }

    private TaskOptionsRecord getTaskOption(Long optionId, Long taskId) {
        return dsl.selectFrom(TASK_OPTIONS)
                .where(TASK_OPTIONS.ID.eq(optionId))
                .and(TASK_OPTIONS.TASK_ID.eq(taskId))
                .fetchOptional()
                .orElseThrow(() -> new InvalidAnswerException("Invalid option for this task"));
    }

    private TaskErrorItemsRecord getTaskErrorItem(Long errorItemId, Long taskId) {
        return dsl.selectFrom(TASK_ERROR_ITEMS)
                .where(TASK_ERROR_ITEMS.ID.eq(errorItemId))
                .and(TASK_ERROR_ITEMS.TASK_ID.eq(taskId))
                .fetchOptional()
                .orElseThrow(() -> new InvalidAnswerException("Invalid error item for this task"));
    }
}
