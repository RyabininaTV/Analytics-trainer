package com.example.progress_and_profile.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidOldPasswordException extends WebApplicationException {

    public InvalidOldPasswordException() {
        super("Old password is invalid", Response.Status.BAD_REQUEST);
    }

}
