package com.example.utils;

import com.example.jooq.generated.tables.records.UserProgressRecord;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.experimental.UtilityClass;
import org.jooq.Field;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.jooq.generated.tables.UserProgress.USER_PROGRESS;

@UtilityClass
public class ProgressStatUtil {

    @Nonnull
    public static Stat stat() {
        return Stat.builder()
                .completedTasksCount(coalesceMax(USER_PROGRESS.COMPLETED_TASKS_COUNT))
                .totalTasksCount(coalesceMax(USER_PROGRESS.TOTAL_TASKS_COUNT))
                .totalScore(coalesceMax(USER_PROGRESS.TOTAL_SCORE))
                .lastActivityAt(DSL.max(USER_PROGRESS.LAST_ACTIVITY_AT))
                .build();
    }

    @Nonnull
    private static Field<Integer> coalesceMax(TableField<UserProgressRecord, Integer> field) {
        return DSL.coalesce(DSL.max(field), BigDecimal.ZERO).cast(Integer.class);
    }

    @Builder
    public record Stat(

            Field<Integer> completedTasksCount,
            Field<Integer> totalTasksCount,
            Field<Integer> totalScore,
            Field<LocalDateTime> lastActivityAt

    ) {}

}
