package com.example.admin;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AdminEndpoints {

    public static final String BASE = "/admin";

    public static final String CREATE_TRAINER = "/trainers";
    public static final String UPDATE_BY_ID = CREATE_TRAINER + "/{id}";

    public static final String CREATE_TASK = "/tasks";
    public static final String UPDATE_TASK = CREATE_TASK + "/{id}";
    public static final String DELETE_TASK = CREATE_TASK + "/{id}";

}
