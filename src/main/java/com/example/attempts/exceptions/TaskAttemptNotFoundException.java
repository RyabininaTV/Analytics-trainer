package com.example.attempts.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TaskAttemptNotFoundException extends WebApplicationException {

    public TaskAttemptNotFoundException(long taskId) {
        super("No attempt found for task with id: " + taskId, Response.Status.NOT_FOUND);
    }
}
