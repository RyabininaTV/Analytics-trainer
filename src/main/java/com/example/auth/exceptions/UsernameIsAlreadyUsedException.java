package com.example.auth.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UsernameIsAlreadyUsedException extends WebApplicationException {

    public UsernameIsAlreadyUsedException() {
        super("Username is already used", Response.Status.CONFLICT);
    }

}
