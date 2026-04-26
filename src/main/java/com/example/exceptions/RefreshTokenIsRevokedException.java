package com.example.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class RefreshTokenIsRevokedException extends WebApplicationException {

    public RefreshTokenIsRevokedException() {
        super("Refresh token is revoked", Response.Status.UNAUTHORIZED);
    }

}
