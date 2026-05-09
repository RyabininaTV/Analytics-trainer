package com.example.tasks;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TasksEndpoints {

    public static final String BASE = "/tasks";

    public static final String TASK_DETAILS = "/{id}";
    public static final String RANDOM = "/random";

}
