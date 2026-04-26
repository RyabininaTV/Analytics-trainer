package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UserIsBlockedException extends WebApplicationException {

    public UserIsBlockedException() {
        super("User is blocked", Response.Status.FORBIDDEN);
    }

}
