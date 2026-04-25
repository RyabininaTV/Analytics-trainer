package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidRefreshTokenException extends WebApplicationException {

    public InvalidRefreshTokenException() {
        super("Invalid refresh token", Response.Status.UNAUTHORIZED);
    }

}
