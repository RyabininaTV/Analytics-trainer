package com.example.attempts.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidAnswerException extends WebApplicationException {

    public InvalidAnswerException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("INVALID_ANSWER", message))
                .build());
    }

    private record ErrorResponse(String code, String message) {}
}
