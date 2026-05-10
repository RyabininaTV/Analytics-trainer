package com.example.admin.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidTaskPayloadException extends WebApplicationException {

    public InvalidTaskPayloadException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }

}
