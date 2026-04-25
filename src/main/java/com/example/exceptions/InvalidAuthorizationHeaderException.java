package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidAuthorizationHeaderException extends WebApplicationException {

    public InvalidAuthorizationHeaderException() {
        super("Authorization header is invalid", Response.Status.UNAUTHORIZED);
    }

}
