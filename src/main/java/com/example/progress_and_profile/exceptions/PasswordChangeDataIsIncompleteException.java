package com.example.progress_and_profile.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class PasswordChangeDataIsIncompleteException extends WebApplicationException {

    public PasswordChangeDataIsIncompleteException() {
        super("Old password and new password are required for password change", Response.Status.BAD_REQUEST);
    }

}
