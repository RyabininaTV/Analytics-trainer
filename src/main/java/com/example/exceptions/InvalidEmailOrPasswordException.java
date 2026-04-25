package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidEmailOrPasswordException extends WebApplicationException {

    public InvalidEmailOrPasswordException() {
        super("Invalid email or password", Response.Status.UNAUTHORIZED);
    }

}
