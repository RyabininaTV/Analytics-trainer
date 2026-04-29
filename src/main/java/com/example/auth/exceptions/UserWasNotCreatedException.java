package com.example.auth.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UserWasNotCreatedException extends WebApplicationException {

    public UserWasNotCreatedException() {
        super("User was not created", Response.Status.INTERNAL_SERVER_ERROR);
    }

}
