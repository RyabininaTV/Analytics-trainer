package com.example.task;

import com.example.jooq.generated.enums.TaskTypeEnum;
import com.example.task.model.BasicTask;
import com.example.task.model.TaskDetails;
import lombok.experimental.UtilityClass;
import org.jooq.Record4;
import org.jooq.Record8;

import static com.example.jooq.generated.tables.Tasks.TASKS;

@UtilityClass
public class Mapper {

    BasicTask toBasicTask(Record4<Long, Long, TaskTypeEnum, String> record) {
       return BasicTask.builder()
                .id(record.get(TASKS.ID))
                .trainerId(record.get(TASKS.SIMULATOR_ID))
                .type(record.get(TASKS.TASK_TYPE))
                .title(record.get(TASKS.TITLE))
                .build();
    }

    TaskDetails toTaskDetails(Record8<Long, Long, TaskTypeEnum, String, String, String, Integer, Boolean> record) {
        return TaskDetails.builder()
                .id(record.get(TASKS.ID))
                .trainerId(record.get(TASKS.SIMULATOR_ID))
                .type(record.get(TASKS.TASK_TYPE))
                .title(record.get(TASKS.TITLE))
                .description(record.get(TASKS.DESCRIPTION))
                .content(record.get(TASKS.CONTENT))
                .maxScore(record.get(TASKS.MAX_SCORE))
                .autoCheck(record.get(TASKS.AUTO_CHECK_ENABLED))
                .build();
    }
}
