package com.example.task.domain.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TaskNotFoundException extends WebApplicationException {
    public TaskNotFoundException() {
        super("Task not found with random id", Response.Status.NOT_FOUND);
    }
}
