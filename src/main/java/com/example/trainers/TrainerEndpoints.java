package com.example.trainers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TrainerEndpoints {

    public static final String BASE = "/trainers";

    public static final String GET_ALL_TRAINERS = "/get-all";
    public static final String GET_TRAINER_INFO_BY_ID = "/{id}";
    public static final String GET_TRAINER_TASKS_BY_ID = "/{id}/tasks";
    public static final String GET_USER_PROGRESS_BY_TRAINER_ID = "/{id}/progress";
    public static final String RESET_USER_PROGRESS_BY_TRAINER_ID = "/{id}/reset";

}
