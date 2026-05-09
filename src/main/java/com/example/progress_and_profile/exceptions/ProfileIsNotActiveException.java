package com.example.progress_and_profile.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ProfileIsNotActiveException extends WebApplicationException {

    public ProfileIsNotActiveException() {
        super("Profile is not active", Response.Status.FORBIDDEN);
    }

}
