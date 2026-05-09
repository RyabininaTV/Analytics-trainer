package com.example.attempts;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AttemptEndpoints {
    public static final String BASE = "/attempts";

    public static final String GET_USER_ATTEMPTS = "";
    public static final String GET_ATTEMPT_BY_TASK_ID = "/{taskId}";
    public static final String GET_ATTEMPT_DETAILS = "/{id}/details";
    public static final String SUBMIT_ATTEMPT = "";
}
