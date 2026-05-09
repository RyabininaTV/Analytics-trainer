package com.example.progress_and_profile.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class EmptyProfileUpdateException extends WebApplicationException {

    public EmptyProfileUpdateException() {
        super("No profile fields to update", Response.Status.BAD_REQUEST);
    }

}
