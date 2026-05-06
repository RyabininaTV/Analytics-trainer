package com.example.attempts.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TaskNotFoundException extends WebApplicationException {

    public TaskNotFoundException(long id) {
        super("Task not found by id: " + id, Response.Status.NOT_FOUND);
    }
}
