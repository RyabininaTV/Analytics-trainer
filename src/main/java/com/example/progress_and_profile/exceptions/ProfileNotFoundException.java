package com.example.progress_and_profile.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ProfileNotFoundException extends WebApplicationException {

    public ProfileNotFoundException() {
        super("Profile not found", Response.Status.NOT_FOUND);
    }

}
