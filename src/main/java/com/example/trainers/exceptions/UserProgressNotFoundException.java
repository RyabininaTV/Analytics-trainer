package com.example.trainers.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UserProgressNotFoundException extends WebApplicationException {

    public UserProgressNotFoundException() {
        super("User progress not found", Response.Status.NOT_FOUND);
    }

}
