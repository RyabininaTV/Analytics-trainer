package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class EmailIsAlreadyUsedException extends WebApplicationException {

    public EmailIsAlreadyUsedException() {
        super("Email is already used", Response.Status.CONFLICT);
    }

}
