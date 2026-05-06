package com.example.attempts.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class AttemptNotFoundException extends WebApplicationException {

    public AttemptNotFoundException(long id) {
        super("Attempt not found by id: " + id, Response.Status.NOT_FOUND);
    }

    public AttemptNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
}
